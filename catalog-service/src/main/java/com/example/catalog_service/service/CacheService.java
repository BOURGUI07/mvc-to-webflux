package com.example.catalog_service.service;

import com.example.catalog_service.domain.Product;
import com.example.catalog_service.exceptions.ApplicationsExceptions;
import com.example.catalog_service.repo.ProductRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final ProductRepo repo;
    private final RedissonReactiveClient redissonClient;
    private RMapReactive<String, Product> mapCode;
    private RMapReactive<Long, Product> mapId;

    @PostConstruct
    public void initializeMap() {
        this.mapCode = redissonClient.getMap("productsCode",new TypedJsonJacksonCodec(String.class,Product.class));
        this.mapId = redissonClient.getMap("productsId",new TypedJsonJacksonCodec(Long.class,Product.class));
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void evictCache(){
        mapCode.readAllKeySet()
                .as(Flux::from)
                .flatMap(Flux::fromIterable)
                .flatMap(mapCode::fastRemove)
                .subscribe();
    }

    @Scheduled(fixedRate = 10_000)
    public void updateCache(){
        repo.findAll()
                .collectList()
                .map(list -> list.stream().collect(Collectors.toMap(Product::getCode, Function.identity())))
                .flatMap(mapCode::putAll)
                .subscribe();
    }

    public Mono<Product> findByCode(String code){
        return mapCode.get(code)
                .switchIfEmpty(repo.findByCodeIgnoreCase(code)
                        .switchIfEmpty(ApplicationsExceptions.productNotFound(code))
                        .flatMap(product -> mapCode.fastPut(code,product).thenReturn(product))
                );
    }

    public Mono<Product> findById(Long id){
        return mapId.get(id)
                .switchIfEmpty(repo.findById(id)
                        .switchIfEmpty(ApplicationsExceptions.productNotFound(id))
                        .flatMap(product -> mapId.fastPut(id,product).thenReturn(product))
                );
    }


    public Mono<Long> doOnChanged(Product product){
        return mapCode.fastRemove(product.getCode());
    }

    public Flux<Product> findAll(){
        return mapCode.readAllValues()
                .as(Flux::from)
                .flatMap(Flux::fromIterable);
    }




}
