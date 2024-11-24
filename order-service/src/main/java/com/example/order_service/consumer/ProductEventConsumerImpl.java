package com.example.order_service.consumer;

import com.example.order_service.events.ProductEvent;
import com.example.order_service.events.ProductEventConsumer;
import com.example.order_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEventConsumerImpl implements ProductEventConsumer {
    private final ProductService service;

    @Override
    public Mono<Void> handle(ProductEvent.View productEvent) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> handle(ProductEvent.Created productEvent) {
        return service.createProduct(productEvent);
    }

    @Override
    public Mono<Void> handle(ProductEvent.Updated productEvent) {
        return service.updateProduct(productEvent);
    }

    @Override
    public Mono<Void> handle(ProductEvent.Deleted productEvent) {
        return service.deleteProduct(productEvent);
    }
}
