package com.example.catalog_service.service.cache;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractCacheService<KEY, VALUE> implements CacheTemplate<KEY, VALUE> {

    public abstract Mono<VALUE> getFromSource(KEY key);
    public abstract Mono<VALUE> getFromCache(KEY key);
    public abstract Flux<VALUE> getAllFromCache();
    public abstract Mono<Boolean> putInCache(KEY key, VALUE value);
    public abstract Mono<Long> removeFromCache(VALUE value);


    /**
     * Retrieve value from cache
     * if empty, retrieve it from the source(usually DB)
     * put the value in cache
     * then return the retrieved value
     */
    @Override
    public Mono<VALUE> findByKey(KEY key) {
        return getFromCache(key)
                .switchIfEmpty(getFromSource(key)
                        .flatMap(value -> putInCache(key, value).thenReturn(value))
                );
    }

    /**
     * Retrieve all values from cache
     */
    @Override
    public Flux<VALUE> findAll() {
        return getAllFromCache();
    }


    /**
     * Once a value is either deleted/updated, remove it from cache
     */
    @Override
    public Mono<Long> doOnChanged(VALUE value) {
        return removeFromCache(value);
    }
}
