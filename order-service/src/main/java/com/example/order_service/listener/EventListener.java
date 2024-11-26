package com.example.order_service.listener;

import com.example.order_service.events.OrderSaga;
import reactor.core.publisher.Mono;

public interface EventListener<T extends OrderSaga> {

    Mono<Void> listen(T event);
}
