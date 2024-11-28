package com.example.order_service.cache;

import java.util.UUID;
import reactor.core.publisher.Mono;

public interface CacheService<T> {

    Mono<T> findById(UUID orderId);

    Mono<Long> doOnChanged(T t);
}
