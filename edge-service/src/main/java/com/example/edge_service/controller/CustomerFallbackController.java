package com.example.edge_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customer-fallback")
public class CustomerFallbackController extends AbstractFallback{

    @GetMapping
    public Mono<ResponseEntity<String>> getCustomerFallback() {
        return fallback().apply("GET");
    }

    @PostMapping
    public Mono<ResponseEntity<String>> postCustomerFallback() {
        return fallback().apply("POST");
    }

}
