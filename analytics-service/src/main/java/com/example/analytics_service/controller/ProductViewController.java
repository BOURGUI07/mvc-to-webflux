package com.example.analytics_service.controller;

import com.example.analytics_service.dto.ProductViewDTO;
import com.example.analytics_service.service.ProductViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analytics")
public class ProductViewController {

    private final ProductViewService service;


    @GetMapping(value = "/views")
    public Flux<ProductViewDTO> products(){
        return service.products();
    }
}