package com.example.edge_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customer-fallback")
@Slf4j
public class CustomerFallbackController {

    private static final String MESSAGE = "This is a Customer Service fallback for GET method";

    @GetMapping
    public Mono<String> getCustomerFallback() {
        return Mono.fromSupplier(() -> MESSAGE)
                .doFirst(() -> log.info("Executing Fallback for Get method"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public void postCustomerFallback() {
        log.info("Executing Fallback for Post method");
    }

}
