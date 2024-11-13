package com.example.catalog_service.service;

import com.example.catalog_service.domain.Product;
import com.example.catalog_service.dto.*;
import com.example.catalog_service.exceptions.ApplicationsExceptions;
import com.example.catalog_service.mapper.Mapper;
import com.example.catalog_service.repo.ProductRepo;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.example.catalog_service.validator.CreationRequestValidator;
import com.example.catalog_service.validator.UpdateRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.service.RequestBodyService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepo repo;
    private final CatalogServiceProperties properties;
    private final UpdateRequestValidator updateValidator;
    private final CreationRequestValidator creationValidator;
    private final RequestBodyService requestBodyBuilder;

    public Flux<ProductResponse> getProductStream(BigDecimal maxPrice) {
        return repo.findAll()
                .map(Mapper.toDto())
                .filter(x -> x.price().compareTo(maxPrice) < 0)
                .doFirst(()-> log.info("......Service Layer::Get Products Stream of Max Price: {} ......", maxPrice));
    }

    public Mono<PagedResult<ProductResponse>> getProducts(int page) {
        var pageNumber = page >= 1 ? page - 1 : 0;
        var sort = Sort.by("name").ascending();
        var pageable = PageRequest.of(pageNumber, properties.defaultPageSize(), sort);
        return Mono.zip(repo.findBy(pageable).map(Mapper.toDto()).collectList(), repo.count())
                .map(x -> Mapper.toPagedResult(x.getT1(), x.getT2(), pageNumber, properties.defaultPageSize()))
                .doFirst(()->log.info(
                        "......Service Layer::Get Products Of Page: {} And Size: {} ......",
                        page,
                        properties.defaultPageSize()));
    }

    public Mono<ProductResponse> findByCode(String code) {

        return  repo.findByCodeIgnoreCase(code)
                .switchIfEmpty(ApplicationsExceptions.productNotFound(code))
                .map(Mapper.toDto())
                .doFirst(()-> log.info("......Service Layer::Get Product By Code: {} ......", code));
    }


    @Transactional
    public Mono<ProductResponse> createProduct(Mono<ProductCreationRequest> request) {
        return request
                .transform(creationValidator.validate())
                .map(Mapper.toEntity())
                .flatMap(repo::save)
                .map(Mapper.toDto())
                .doFirst(() -> log.info("......Service Layer::Create Product: {}", request));
    }


    @Transactional
    public Mono<ProductResponse> update(String code, Mono<ProductUpdateRequest> request) {
        return repo.findByCodeIgnoreCase(code)
                .switchIfEmpty(ApplicationsExceptions.productNotFound(code))
                .zipWhen(product -> request.transform(updateValidator.validate()),processUpdate())
                .flatMap(Function.identity())
                .doFirst(() -> log.info("......Service Layer::Update Product With Code: {}. Update Request: {}",code ,request));
    }


    private BiFunction<Product,ProductUpdateRequest,Mono<ProductResponse>> processUpdate() {
        return (product,request) -> Mono.fromSupplier(() -> product.setDescription(request.description())
                .setPrice(request.price())
                .setName(request.name())
                .setImageUrl(request.imageUrl()))
                .flatMap(repo::save)
                .map(Mapper.toDto());
    }


    @Transactional
    public Mono<Void> deleteByCode(String code){
        return  repo.findByCodeIgnoreCase(code)
                .switchIfEmpty(ApplicationsExceptions.productNotFound(code))
                .flatMap(repo::delete)
                .doFirst(() -> log.info("......Service Layer::Delete Product: {}", code));
    }

}
