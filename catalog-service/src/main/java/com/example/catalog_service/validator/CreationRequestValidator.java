package com.example.catalog_service.validator;

import com.example.catalog_service.dto.ProductCreationRequest;
import com.example.catalog_service.exceptions.ApplicationsExceptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@Service
public class CreationRequestValidator extends RequestValidator<ProductCreationRequest> {


    @Override
    public Predicate<ProductCreationRequest> hasName() {
        return dto -> Objects.nonNull(dto.name());
    }

    @Override
    public Predicate<ProductCreationRequest> hasValidPrice() {
        return dto -> Objects.nonNull(dto.price()) && dto.price().doubleValue() > 0;
    }

    @Override
    public Predicate<ProductCreationRequest> hasDescription() {
        return dto -> Objects.nonNull(dto.description());
    }

    @Override
    public Predicate<ProductCreationRequest> hasImageUrl() {
        return dto -> Objects.nonNull(dto.imageUrl());
    }

    public Predicate<ProductCreationRequest> hasCode() {
        return dto -> Objects.nonNull(dto.code());
    }

    @Override
    public UnaryOperator<Mono<ProductCreationRequest>> validate(){
        return dto -> dto
                .filter(hasCode())
                .switchIfEmpty(ApplicationsExceptions.invalidRequest("Product Code is Required"))
                .filter(hasName())
                .switchIfEmpty(ApplicationsExceptions.invalidRequest("Product Name is Required"))
                .filter(hasDescription())
                .switchIfEmpty(ApplicationsExceptions.invalidRequest("Product Description is Required"))
                .filter(hasValidPrice())
                .switchIfEmpty(ApplicationsExceptions.invalidRequest("Product Price is Invalid"));
    }
}
