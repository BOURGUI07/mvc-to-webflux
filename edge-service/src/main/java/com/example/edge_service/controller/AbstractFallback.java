package com.example.edge_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
public abstract class AbstractFallback {

    private static final String MESSAGE = "Service Temporarily Unavailable, Please Try Again Later";

     Function<String, Mono<ResponseEntity<String>>> fallback() {
        return methodName -> Mono.fromSupplier(() ->
                        ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body(MESSAGE)
                )
                .doFirst(() -> log.info("Executing Fallback for %s Method".formatted(methodName)));
    }
}
