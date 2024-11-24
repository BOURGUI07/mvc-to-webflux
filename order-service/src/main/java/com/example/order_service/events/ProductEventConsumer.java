package com.example.order_service.events;

import reactor.core.publisher.Mono;

public interface ProductEventConsumer {

    default Mono<Void> consume(ProductEvent productEvent){
        return switch (productEvent){
            case ProductEvent.Created e -> handle(e);
            case ProductEvent.View e -> handle(e);
            case ProductEvent.Updated e -> handle(e);
            case ProductEvent.Deleted e -> handle(e);
        };
    }

    Mono<Void> handle(ProductEvent.View productEvent);
    Mono<Void> handle(ProductEvent.Created productEvent);
    Mono<Void> handle(ProductEvent.Updated productEvent);
    Mono<Void> handle(ProductEvent.Deleted productEvent);
}
