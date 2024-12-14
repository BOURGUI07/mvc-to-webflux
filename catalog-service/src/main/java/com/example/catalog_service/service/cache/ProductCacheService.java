package com.example.catalog_service.service.cache;

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
public class ProductCacheService extends AbstractCacheService<String,Product>{

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

    public Mono<Product> findByCode(String code){
        return findByKey(code).switchIfEmpty(ApplicationsExceptions.productNotFound(code));
    }

    @Override
    public Mono<Product> getFromSource(String code) {
        return repo.findByCodeIgnoreCase(code);
    }

    @Override
    public Mono<Product> getFromCache(String code) {
        return operations.get(PRODUCT_KEY,code);
    }

    @Override
    public Flux<Product> getAllFromCache() {
        return operations.values(PRODUCT_KEY);
    }

    @Override
    public Mono<Boolean> putInCache(String s, Product product) {
        return operations.putIfAbsent(PRODUCT_KEY,s,product);
    }

    @Override
    public Mono<Long> removeFromCache(Product product) {
        return operations.remove(PRODUCT_KEY,product.getCode());
    }



}
