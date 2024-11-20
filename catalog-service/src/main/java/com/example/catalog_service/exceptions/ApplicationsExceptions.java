package com.example.catalog_service.exceptions;

import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

public class ApplicationsExceptions {
    public static <T> Mono<T> productNotFound(String code) {
        return Mono.error(new ProductNotFoundException(code));
    }

    public static <T> Mono<T> productNotFound(Long productId) {
        return Mono.error(new ProductNotFoundException(productId));
    }

    public static <T> Mono<T> notEnoughInventory(Long productId) {
        return Mono.error(new NotEnoughInventoryException(productId));
    }

    public static <T> Mono<T> invalidRequest(String message) {
        return Mono.error(new InvalidProductRequestException(message));
    }

    public static <T> Mono<T> productAlreadyExists(String code) {
        return Mono.error(new ProductAlreadyExistsException(code));
    }

    public static <T> Mono<T> duplicateEvent(UUID orderId) {
        return Mono.error(new DuplicatedEventException(orderId));
    }

}
