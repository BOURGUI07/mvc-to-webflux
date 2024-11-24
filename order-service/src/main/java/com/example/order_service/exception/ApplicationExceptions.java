package com.example.order_service.exception;

import reactor.core.publisher.Mono;

public class ApplicationExceptions {

    public static <T>Mono<T> invalidRequest(String message) {
        return Mono.error(new InvalidRequestException(message));
    }
}
