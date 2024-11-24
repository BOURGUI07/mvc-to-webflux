package com.example.catalog_service.publisher;

import com.example.catalog_service.events.ProductEvent;
import com.example.catalog_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEventPublisher {
    private final ProductService service;


    public Flux<ProductEvent> publish() {
        return service.products();
    }


    public Flux<ProductEvent> publishViewedProducts() {
        return service.viewedProducts();
    }




}
