package com.example.catalog_service.service;

import com.example.catalog_service.domain.Product;
import com.example.catalog_service.dto.*;
import com.example.catalog_service.exceptions.ApplicationsExceptions;
import com.example.catalog_service.mapper.Mapper;
import com.example.catalog_service.repo.ProductRepo;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import com.example.catalog_service.util.Util;
import com.example.catalog_service.validator.CreationRequestValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.service.RequestBodyService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.expression.spel.ast.FunctionReference;
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



    public Flux<ProductResponse> getProductStream(BigDecimal maxPrice) {
        return repo.findAll()
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

    public Mono<ProductResponse> findByCode(String code) {

        return  repo.findByCodeIgnoreCase(code)
                .switchIfEmpty(ApplicationsExceptions.productNotFound(code))
                .map(Mapper.toDto())
                .doOnNext(response -> log.info("Product with code: {} is: {}", code, Util.write(response)));
    }


    @Transactional
    public Mono<ProductResponse> createProduct(Mono<ProductCreationRequest> request) {
        return request
                .transform(CreationRequestValidator.validate().andThen(validateCodeUniqueness()))
                .map(Mapper.toEntity())
                .flatMap(repo::save)
                .map(Mapper.toDto())
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


    @Transactional
    public Mono<Void> deleteByCode(String code){
        return  repo.findByCodeIgnoreCase(code)
                .switchIfEmpty(ApplicationsExceptions.productNotFound(code))
                .flatMap(repo::delete)
                .doOnSuccess(x-> log.info("Product Deleted Successfully"));
    }


    @Transactional
    public Mono<ProductResponse> update(String code, Mono<ProductUpdateRequest> request) {
        return repo.findByCodeIgnoreCase(code)
                .switchIfEmpty(ApplicationsExceptions.productNotFound(code))
                .zipWhen(product -> request.doOnNext(dto -> log.info("Received Product Update Request: {}",Util.write(dto))))
                .flatMap(tuple -> {
                    var req = tuple.getT2();
                    var product = tuple.getT1();

                    var allFields = Stream.of(req.price(),req.description(), req.name(),req.imageUrl());

                    var allFieldsExist = allFields.allMatch(Objects::nonNull);

                    return allFieldsExist ? processUpdate().apply(product,req) : processNullableUpdate().apply(product,req);

                })
                .doOnNext(response -> log.info("Product Updated Successfully: {}", Util.write(response)));
    }


    private BiFunction<Product,ProductUpdateRequest,Mono<ProductResponse>> processUpdate() {
        return (product,request) -> Mono.fromSupplier(() -> product.setDescription(request.description())
                        .setPrice(request.price())
                        .setName(request.name())
                        .setImageUrl(request.imageUrl()))
                .filter(p -> p.getPrice().doubleValue() > 0)
                .switchIfEmpty(ApplicationsExceptions.invalidRequest("Product Price Must be Positive"))
                .flatMap(repo::save)
                .map(Mapper.toDto());
    }

    private BiFunction<Product,ProductUpdateRequest,Mono<ProductResponse>> processNullableUpdate() {
        return (product,request) -> {
            var optionalRequest = Mapper.toOptional().apply(request);

            optionalRequest.description().ifPresent(product::setDescription);
            optionalRequest.price().ifPresent(product::setPrice);
            optionalRequest.imageUrl().ifPresent(product::setImageUrl);
            optionalRequest.name().ifPresent(product::setName);

            return Mono.fromSupplier(() -> product)
                    .filter(p -> p.getPrice().doubleValue() > 0)
                    .switchIfEmpty(ApplicationsExceptions.invalidRequest("Product Price Must be Positive"))
                    .flatMap(repo::save)
                    .map(Mapper.toDto());

        };

    }



}
