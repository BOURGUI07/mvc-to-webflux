package com.example.catalog_service.exceptions;

import reactor.core.publisher.Mono;

public class ApplicationsExceptions {
    public static <T> Mono<T> productNotFound(String code) {
        return Mono.error(new ProductNotFoundException(code));
    }

    public static <T> Mono<T> invalidRequest(String message) {
        return Mono.error(new InvalidProductRequestException(message));
    }
}
