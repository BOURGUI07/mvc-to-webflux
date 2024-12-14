package com.example.catalog_service.service.cache;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CacheTemplate<KEY,VALUE> {

    /**
     *
     * @param key will be used to retrieve the value either from source of cache
     * @return return Mono of retrieved value
     */
    Mono<VALUE> findByKey(KEY key);


    /**
     * Retrive all values
     * @return Flux of retrieved values
     */
    Flux<VALUE> findAll();


    /**
     * When a value is either deleted or updated, we will remove the value from cache
     * @param value
     * @return
     */
    Mono<Long> doOnChanged(VALUE value);
}
