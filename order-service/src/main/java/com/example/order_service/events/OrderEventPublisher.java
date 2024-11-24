package com.example.order_service.events;

import reactor.core.publisher.Flux;

public interface OrderEventPublisher {

    Flux<OrderEvent> publish();
}
