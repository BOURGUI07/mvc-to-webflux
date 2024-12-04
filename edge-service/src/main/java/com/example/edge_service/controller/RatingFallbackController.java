package com.example.edge_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/rating-fallback")
@Slf4j
public class RatingFallbackController extends AbstractFallback{



    @GetMapping
    public Mono<ResponseEntity<String>> getRatingFallback() {
        return fallback().apply("GET");

    }

    @PostMapping
    public Mono<ResponseEntity<String>> postRatingFallback() {
        return fallback().apply("POST");

    }

    @PutMapping
    public Mono<ResponseEntity<String>> putRatingFallback() {
        return fallback().apply("PUT");

    }

    @DeleteMapping
    public Mono<ResponseEntity<String>> deleteRatingFallback() {
        return fallback().apply("DELETE");

    }






}
