package com.example.edge_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@RestController
@RequestMapping("/catalog-fallback")
@Slf4j
public class CatalogFallbackController extends AbstractFallback{



    @GetMapping
    public Mono<ResponseEntity<String>> getCatalogFallback() {
        return fallback().apply("GET");

    }

    @PostMapping
    public Mono<ResponseEntity<String>> postCatalogFallback() {
        return fallback().apply("POST");

    }

    @PutMapping
    public Mono<ResponseEntity<String>> putCatalogFallback() {
        return fallback().apply("PUT");

    }

    @DeleteMapping
    public Mono<ResponseEntity<String>> deleteCatalogFallback() {
        return fallback().apply("DELETE");

    }






}
