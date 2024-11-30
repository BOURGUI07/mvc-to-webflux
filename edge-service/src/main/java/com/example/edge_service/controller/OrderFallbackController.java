package com.example.edge_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/order-fallback")
public class OrderFallbackController extends AbstractFallback{


    @GetMapping
    public Mono<ResponseEntity<String>> getOrderFallback() {
        return fallback().apply("GET");
    }

    @PostMapping
    public Mono<ResponseEntity<String>> postOrderFallback() {
        return fallback().apply("POST");
    }
}
