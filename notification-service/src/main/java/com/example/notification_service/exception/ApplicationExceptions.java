package com.example.notification_service.exception;

import reactor.core.publisher.Mono;

public class ApplicationExceptions {


    public static <T> Mono<T> duplicateEvent(){
        return Mono.error(new DuplicateEventException());
    }


    public static <T> Mono<T> emailFailure(){
        return Mono.error(new EmailFailureException());
    }
}
