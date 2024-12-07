package com.example.customer_service.events;

import reactor.core.publisher.Mono;

/**
 * The Event process gonna process order-events which are order-saga
 * then publish customer-events which are order-saga as well
 */


public interface EventProcessor<T extends OrderSaga, R extends OrderSaga> {
    Mono<R> process(T event);
}
