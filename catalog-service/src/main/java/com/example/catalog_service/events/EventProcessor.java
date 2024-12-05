package com.example.catalog_service.events;

import reactor.core.publisher.Mono;

/**
 * The Catalog-service gonna consume order-events which extends Order-Saga
 * And it's gonna publish Inventory-Events which extends Order-Saga as well
 */

public interface EventProcessor<T extends OrderSaga, R extends OrderSaga> {
    Mono<R> process(T event);
}
