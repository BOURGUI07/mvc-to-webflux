package com.example.catalog_service.exceptions;

import reactor.core.publisher.Mono;

import java.util.function.Function;

public class ApplicationsExceptions {
    public static <T> Mono<T> productNotFound(String code) {
        return Mono.error(new ProductNotFoundException(code));
    }

    public static <T> Mono<T> invalidRequest(String message) {
        return Mono.error(new InvalidProductRequestException(message));
    }

    public static <T> Mono<T> productAlreadyExists(String code) {
        return Mono.error(new ProductAlreadyExistsException(code));
    }

}
