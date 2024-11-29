package com.example.edge_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/catalog-fallback")
@Slf4j
public class CatalogFallbackController {
    private static final String MESSAGE = "This is a Catalog-Service fallback for GET method";

    @GetMapping
    public Mono<String> getCatalogFallback() {
        return Mono.fromSupplier(() -> MESSAGE)
                .doFirst(() -> log.info("Executing Fallback for Get method"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public void postCatalogFallback() {
        log.info("Executing Fallback for Post method");
    }

    @PutMapping
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public void putCatalogFallback() {
        log.info("Executing Fallback for Put method");
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public void deleteCatalogFallback() {
        log.info("Executing Fallback for Delete method");
    }




}
