package com.example.catalog_service.service;

import com.example.catalog_service.domain.Product;
import com.example.catalog_service.domain.ProductAction;
import com.example.catalog_service.dto.*;
import com.example.catalog_service.exceptions.ApplicationsExceptions;
import com.example.catalog_service.listener.ProductActionListener;
import com.example.catalog_service.mapper.Mapper;
import com.example.catalog_service.repo.ProductRepo;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.*;

import com.example.catalog_service.service.cache.CacheTemplate;
import com.example.catalog_service.util.Util;
import com.example.catalog_service.validator.CreationRequestValidator;
import com.example.catalog_service.validator.PostUpdateProductValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The product-service mainly deals with CRUD operations and sending
 * ViewedProductEvents and CreatedProductEvents to Analytics-Service and Order_Service
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepo repo;
    private final CatalogServiceProperties properties;
    private final CacheTemplate<String,Product> cacheTemplate;
    private final ProductActionListener listener;



    /**
     * Get all the products from cache
     * filter by products whose price is less that maxPrice
     */
    public Flux<ProductResponse> getProductStream(BigDecimal maxPrice) {
        return cacheTemplate.findAll()
                .map(Mapper.toDto())
                .filter(x -> x.price().compareTo(maxPrice) < 0);
    }


    public Mono<PagedResult<ProductResponse>> getProducts(int page) {
        var pageNumber = page >= 1 ? page - 1 : 0;
        var sort = Sort.by("name").ascending();
        var pageable = PageRequest.of(pageNumber, properties.defaultPageSize(), sort);
        return Mono.zip(repo.findBy(pageable).map(Mapper.toDto()).collectList(), repo.count())
                .map(x -> Mapper.toPagedResult(x.getT1(), x.getT2(), pageNumber, properties.defaultPageSize()));

    }


    /**
     * Get the product from cache,
     * convert it to DTO,
     * make it available for productEventListener to subscribe to it.
     */
    public Mono<ProductResponse> findByCode(String code) {
        return cacheTemplate.findByKey(code)
                .map(Mapper.toDto())
                .doOnNext(dto -> listener.listen(Mapper.toProductActionDTO().apply(ProductAction.VIEWED,dto)))
                .doOnNext(response -> log.info("Product with code: {} is: {}", code, Util.write(response)));
    }


    /**
     * Check the request fields Nullability.
     * Then Check the code uniqueness.
     * If all went well, save the product into the DB
     * Convert the newly saved product entity into DTO
     * Asynchronously the Listener will subscribe to it
     */
    @Transactional
    public Mono<ProductResponse> createProduct(Mono<ProductCreationRequest> request) {
        return request
                .transform(CreationRequestValidator.validate().andThen(validateCodeUniqueness()))
                .map(Mapper.toEntity())
                .flatMap(repo::save)
                .map(Mapper.toDto())
                .doOnNext(dto -> listener.listen(Mapper.toProductActionDTO().apply(ProductAction.CREATED,dto)))
                .doOnNext(response -> log.info("A New Product is Created: {}", Util.write(response)));

    }


    private UnaryOperator<Mono<ProductCreationRequest>> validateCodeUniqueness(){
        return request -> request.flatMap(req -> {
                    var code = req.code();
                    return repo.existsByCodeIgnoreCase(code)
                            .doOnNext(booleanValue -> log.info("Does Code Already Exists?: {}",booleanValue))
                            .filter(Predicate.not(b->b))
                            .doOnDiscard(Boolean.class, x -> log.info("Product Creation Process Discarded Product with code: {}", code))
                            .switchIfEmpty(ApplicationsExceptions.productAlreadyExists(code))
                            .thenReturn(req);
                }

                );
    }


    /**
     * Get the code from cache
     * Delete the product from DB. Then from the cache
     * Listener subscribes to the deleted product code
     */
    @Transactional
    public Mono<Void> deleteByCode(String code){
        return  cacheTemplate.findByKey(code)
                .flatMap(product -> repo.delete(product).then(cacheTemplate.doOnChanged(product)).then())
                .doOnSuccess(x->{
                    listener.handleDeletedProduct(Mapper.productActionDTO(ProductAction.DELETED,code));
                    log.info("Product Deleted Successfully");
                });
    }


    /**
     * After Saving the product into the database,
     * Listener subscribes to response dto
     */
    @Transactional
    public Mono<ProductResponse> update(String code, Mono<ProductUpdateRequest> request) {
        return cacheTemplate.findByKey(code)
                .zipWhen(product -> request.transform(print()),processUpdate())
                .flatMap(Function.identity())
                .doOnNext(response -> {
                    log.info("Product Updated Successfully: {}", Util.write(response));
                    listener.handleUpdatedProduct(Mapper.toProductActionDTO().apply(ProductAction.UPDATED,response));
                });
    }

    private UnaryOperator<Mono<ProductUpdateRequest>> print(){
        return mono -> mono
                .doOnNext(dto -> log.info("Received Product Update Request: {}",Util.write(dto)));
    }

    /**
     * The client has the option of updating at least ONE field.
     * He's NOT obliged to update ALL fields.
     * After updating the product, validate its price and quantity
     * If no problems encountered, save the product into DB
     * Then remove the product from the cache
     */
    private BiFunction<Product,ProductUpdateRequest,Mono<ProductResponse>> processUpdate(){
        return (product,request) ->
            Mono.fromSupplier(() -> {
                        Optional.ofNullable(request.description()).ifPresent(product::setDescription);
                        Optional.ofNullable(request.name()).ifPresent(product::setName);
                        Optional.ofNullable(request.price()).ifPresent(product::setPrice);
                        Optional.ofNullable(request.imageUrl()).ifPresent(product::setImageUrl);
                        Optional.ofNullable(request.quantity()).ifPresent(product::setAvailableQuantity);
                return product;
            })
                    .transform(PostUpdateProductValidator.validate())
                    .flatMap(p -> repo.save(p).then(cacheTemplate.doOnChanged(p).thenReturn(p)))
                    .map(Mapper.toDto());

    }



}
