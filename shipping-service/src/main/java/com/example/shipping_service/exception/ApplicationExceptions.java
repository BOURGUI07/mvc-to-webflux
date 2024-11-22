package com.example.shipping_service.exception;

import reactor.core.publisher.Mono;

import java.util.UUID;

public class ApplicationExceptions {

    public static <T>Mono<T> duplicateEvent(){
        return Mono.error(new DuplicateEventException());
    }

    public static <T>Mono<T> quantityLimit(UUID orderId){
        return Mono.error(new QuantityLimitException(orderId));
    }
}
