package com.example.edge_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/analytics-fallback")
public class AnalyticsFallbackController extends AbstractFallback{


    @GetMapping
    public Mono<ResponseEntity<String>> getAnalyticsFallback() {
        return fallback().apply("GET");

    }
}
