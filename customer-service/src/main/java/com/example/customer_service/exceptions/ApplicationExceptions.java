package com.example.customer_service.exceptions;

import reactor.core.publisher.Mono;

import java.util.UUID;

public class ApplicationExceptions {

    public static <T>Mono<T> customerNotFound(Long customerId){
        return Mono.error(new CustomerNotFoundException(customerId));
    }

    public static <T>Mono<T> notEnoughBalance(Long customerId){
        return Mono.error(new NotEnoughBalanceException(customerId));
    }

    public static <T>Mono<T> duplicateEvent(UUID orderId){
        return Mono.error(new DuplicateEventException(orderId));
    }

    public static <T>Mono<T> alreadyExists(String message){
        return Mono.error(new CustomerAlreadyExistsException(message));
    }

    public static <T>Mono<T> invalidRequest(String message){
        return Mono.error(new InvalidRequestException(message));
    }

}
