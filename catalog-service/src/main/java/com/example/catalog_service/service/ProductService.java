package com.example.catalog_service.service;

import com.example.catalog_service.domain.Product;
import com.example.catalog_service.dto.*;
import com.example.catalog_service.events.ProductEvent;
import com.example.catalog_service.exceptions.ApplicationsExceptions;
import com.example.catalog_service.mapper.Mapper;
import com.example.catalog_service.repo.ProductRepo;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.*;

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
import reactor.core.publisher.Sinks;

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

    private final Sinks.Many<ProductEvent> sink = Sinks.many().unicast().onBackpressureBuffer();
    private final Sinks.Many<ProductEvent> viewedProductsSink = Sinks.many().unicast().onBackpressureBuffer();

    private final CacheService cacheService;

    /**
     * This is gonna be used by the CreatedProductEventPublisher
     */
    public Flux<ProductEvent> products(){
        return sink.asFlux();
    }

    /**
     * This is gonna be used by the ViewedProductEventPublisher
     */
    public Flux<ProductEvent> viewedProducts(){
        return viewedProductsSink.asFlux();
    }


    /**
     * Get all the products from cache
     * filter by products whose price is less that maxPrice
     */
    public Flux<ProductResponse> getProductStream(BigDecimal maxPrice) {
        return cacheService.findAll()
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
     * The moment this endpoint is triggered, a ViewedProductEvent gonna
     * be published to analytics-service
     */
    public Mono<ProductResponse> findByCode(String code) {
        return cacheService.findByCode(code)
                .map(Mapper.toDto())
                .doOnNext(dto -> viewedProductsSink.tryEmitNext(ProductEvent.View.builder().code(code).build()))
                .doOnNext(response -> log.info("Product with code: {} is: {}", code, Util.write(response)));
    }


    /**
     * Check the request fields Nullability.
     * Then Check the code uniqueness.
     * If all went well, save the product into the DB
     * Convert the newly saved product entity into DTO
     * Convert That DTO into a CreatedProductEvent
     * Then emit the event via the sink.
     */
    @Transactional
    public Mono<ProductResponse> createProduct(Mono<ProductCreationRequest> request) {
        return request
                .transform(CreationRequestValidator.validate().andThen(validateCodeUniqueness()))
                .map(Mapper.toEntity())
                .flatMap(repo::save)
                .map(Mapper.toDto())
                .doOnNext(dto -> sink.tryEmitNext(Mapper.toCreatedProductEvent().apply(dto)))
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
     * Send a DeletedProductEvent to Order-Service
     */
    @Transactional
    public Mono<Void> deleteByCode(String code){
        return  cacheService.findByCode(code)
                .flatMap(product -> repo.delete(product).then(cacheService.doOnChanged(product)).then())
                .doOnSuccess(x->{
                    sink.tryEmitNext(Mapper.toDeletedProductEvent().apply(code));
                    log.info("Product Deleted Successfully");
                });
    }


    /**
     * After Saving the product into the database,
     * Send UpdatedProductEvent to OrderService.
     */
    @Transactional
    public Mono<ProductResponse> update(String code, Mono<ProductUpdateRequest> request) {
        return cacheService.findByCode(code)
                .zipWhen(product -> request.transform(print()),processUpdate())
                .flatMap(Function.identity())
                .doOnNext(response -> {
                    log.info("Product Updated Successfully: {}", Util.write(response));
                    sink.tryEmitNext(Mapper.toUpdatedProductEvent().apply(response));
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
                    .flatMap(p -> repo.save(p).then(cacheService.doOnChanged(p).thenReturn(p)))
                    .map(Mapper.toDto());

    }



}
