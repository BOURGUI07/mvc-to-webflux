package com.example.catalog_service.service;

import com.example.catalog_service.domain.Product;
import com.example.catalog_service.exceptions.ApplicationsExceptions;
import com.example.catalog_service.repo.ProductRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import static com.example.catalog_service.util.Constants.RedisKeys.PRODUCT_KEY;


@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private final ProductRepo repo;
    private final ReactiveHashOperations<String, String, Product> operations;

    /**
     * Every day, at 12 am, remove cache
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void evictCache(){
        operations.delete(PRODUCT_KEY)
                .doOnError(ex -> log.info("Failed to evict cache"))
                .subscribe();
    }

    /**
     * For every 10 secs, persist absent values in the cache
     */
    @Scheduled(fixedRate = 10_000)
    public void updateCache(){
        repo.findAll()
                .flatMap(p -> operations.putIfAbsent(PRODUCT_KEY,p.getCode(),p))
                .doOnError(ex -> log.info("Failed to update cache"))
                .subscribe();
    }

    /**
     *  Retrieve from cache first.
     *  if Absent, retrieve from DB.
     *  persist the DB-retrieved value into the cache
     *  return  the value.
     */
    public Mono<Product> findByCode(String code){
        return operations.get(PRODUCT_KEY,code)
                .switchIfEmpty(repo.findByCodeIgnoreCase(code)
                        .flatMap(product -> operations.put(PRODUCT_KEY,code,product).thenReturn(product))
                        .switchIfEmpty(ApplicationsExceptions.productNotFound(code))
                );
    }

    /**
     * Whenever a product is either updated or removed, remove it from cache
     */
    public Mono<Long> doOnChanged(Product product){
        return operations.remove(PRODUCT_KEY,product.getCode());
    }


    /**
     * Return from cache.
     */
    public Flux<Product> findAll(){
        return operations.values(PRODUCT_KEY);
    }




}
