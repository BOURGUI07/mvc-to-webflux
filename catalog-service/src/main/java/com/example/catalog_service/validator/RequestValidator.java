package com.example.catalog_service.validator;

import com.example.catalog_service.exceptions.ApplicationsExceptions;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public abstract class RequestValidator<T>{

    public abstract  Predicate<T> hasName();

    public abstract Predicate<T> hasValidPrice();

    public abstract Predicate<T> hasDescription();

    public abstract Predicate<T> hasImageUrl();

    public  UnaryOperator<Mono<T>> validate(){
        return T -> T.
                filter(hasValidPrice())
                .switchIfEmpty(ApplicationsExceptions.invalidRequest("Invalid Price"))
                .filter(hasName())
                .switchIfEmpty(ApplicationsExceptions.invalidRequest("Product Name is Required"))
                .filter(hasDescription())
                .switchIfEmpty(ApplicationsExceptions.invalidRequest("Product Description is Required"))
                .filter(hasImageUrl())
                .switchIfEmpty(ApplicationsExceptions.invalidRequest("Product Image Url is Required"));
    }
}
