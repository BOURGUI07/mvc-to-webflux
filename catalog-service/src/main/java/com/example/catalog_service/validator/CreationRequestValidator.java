package com.example.catalog_service.validator;

import com.example.catalog_service.dto.ProductCreationRequest;
import com.example.catalog_service.exceptions.ApplicationsExceptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * The ProductCreationRequest has to have valid fields.
 * All the fields has to be non-null.
 * The product-price as well as the product-quantity should be greater than 0
 */


@Service
public class CreationRequestValidator {


    public static Predicate<ProductCreationRequest> hasName() {
        return dto -> Objects.nonNull(dto.name());
    }

    public static Predicate<ProductCreationRequest> hasValidPrice() {
        return dto -> Objects.nonNull(dto.price()) && dto.price().doubleValue() > 0;
    }

    public static Predicate<ProductCreationRequest> hasValidQuantity() {
        return dto -> Objects.nonNull(dto.quantity()) && dto.quantity() > 0;
    }

    public static Predicate<ProductCreationRequest> hasDescription() {
        return dto -> Objects.nonNull(dto.description());
    }

    public static Predicate<ProductCreationRequest> hasImageUrl() {
        return dto -> Objects.nonNull(dto.imageUrl());
    }

    public static Predicate<ProductCreationRequest> hasCode() {
        return dto -> Objects.nonNull(dto.code());
    }

    public static UnaryOperator<Mono<ProductCreationRequest>> validate(){
        return dto -> dto
                .filter(hasCode())
                .switchIfEmpty(ApplicationsExceptions.invalidRequest("Product Code is Required"))
                .filter(hasName())
                .switchIfEmpty(ApplicationsExceptions.invalidRequest("Product Name is Required"))
                .filter(hasDescription())
                .switchIfEmpty(ApplicationsExceptions.invalidRequest("Product Description is Required"))
                .filter(hasValidPrice())
                .switchIfEmpty(ApplicationsExceptions.invalidRequest("Product Price is Invalid"))
                .filter(hasValidQuantity())
                .switchIfEmpty(ApplicationsExceptions.invalidRequest("Product Quantity is Invalid"))
                .filter(hasImageUrl())
                .switchIfEmpty(ApplicationsExceptions.invalidRequest("Product Image Url is Required"));
    }
}
