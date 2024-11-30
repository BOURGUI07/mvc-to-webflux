package com.example.analytics_service.controller;

import com.example.analytics_service.dto.ProductViewDTO;
import com.example.analytics_service.service.ProductViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.example.analytics_service.util.Constants.PublicApiUrls.PRODUCT_VIEW_BASE_URL;

@RestController
@RequiredArgsConstructor
@RequestMapping(PRODUCT_VIEW_BASE_URL)
public class ProductViewController {

    private final ProductViewService service;


    @GetMapping(value = "/views")
    public Mono<ResponseEntity<Flux<ProductViewDTO>>> products(){
        return Mono.fromSupplier(() -> ResponseEntity.ok(service.products()));
    }
}