package com.example.analytics_service.consumer;

import com.example.analytics_service.events.ProductEvent;
import com.example.analytics_service.service.ProductViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductViewConsumerImpl {
    private final ProductViewService service;

    public Mono<Void> handle(ProductEvent event) {
        return service.consume().apply(((ProductEvent.View) event));
    }
}
