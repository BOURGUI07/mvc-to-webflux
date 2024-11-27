package com.example.order_service.service.cache;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CacheService<T> {

    Mono<T> findById(UUID orderId);

    Mono<Long> doOnChanged(T t);
}
