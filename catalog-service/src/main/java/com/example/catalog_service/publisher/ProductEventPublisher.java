package com.example.catalog_service.publisher;

import com.example.catalog_service.events.ProductEvent;
import com.example.catalog_service.listener.ProductEventListener;
import com.example.catalog_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * The analytics-service is interested in viewedProducts
 * The order-service is interested in product events.
 * Be it created, updated, or removed.
 * It has to track the product prices.
 * I've avoided network calls to use Event Carried State Transfer Pattern
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEventPublisher {
    private final ProductEventListener listener;


    public Flux<ProductEvent> publish() {
        return listener.productEvents();
    }





}
