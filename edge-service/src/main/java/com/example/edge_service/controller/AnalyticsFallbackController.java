package com.example.edge_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/analytics-fallback")
@Slf4j
public class AnalyticsFallbackController {

    private static final String MESSAGE = "This is a Analytics-Service fallback for GET method";

    @GetMapping
    public Mono<String> getAnalyticsFallback() {
        return Mono.fromSupplier(() -> MESSAGE)
                .doFirst(() -> log.info("Executing Fallback for Get method"));
    }
}
