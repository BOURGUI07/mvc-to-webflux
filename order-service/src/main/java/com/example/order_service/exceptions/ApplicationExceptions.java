package com.example.order_service.exceptions;

import reactor.core.publisher.Mono;

public class ApplicationExceptions {
    public static <T> Mono<T> orderNotFound(String orderNumber){
        return Mono.error(new OrderNotFoundException(orderNumber));
    }
}
