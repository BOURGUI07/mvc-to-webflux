package com.example.order_service.events;

import reactor.core.publisher.Mono;

public interface EventProcessor<T extends OrderSaga,R extends OrderSaga> {

    Mono<R> process(T event);
}
