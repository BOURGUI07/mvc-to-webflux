package com.example.order_service.service.cache;

import com.example.order_service.entity.OrderInventory;
import com.example.order_service.entity.PurchaseOrder;
import com.example.order_service.repo.InventoryRepo;
import com.example.order_service.repo.PurchaseOrderRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCacheService implements CacheService<PurchaseOrder> {
    private final PurchaseOrderRepo repo;
    private final RedissonReactiveClient redissonClient;
    private RMapReactive<UUID, PurchaseOrder> map;

    @PostConstruct
    public void initializeMap() {
        this.map = redissonClient.getMap("order",new TypedJsonJacksonCodec(UUID.class,PurchaseOrder.class));
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void evictCache(){
        map.readAllKeySet()
                .as(Flux::from)
                .flatMap(Flux::fromIterable)
                .flatMap(map::fastRemove)
                .subscribe();
    }

    @Override
    public Mono<PurchaseOrder> findById(UUID id) {
        return map.get(id)
                .switchIfEmpty(repo.findByOrderId(id)
                        .flatMap(order -> map.fastPut(id,order).thenReturn(order))
                );
    }

    @Override
    public Mono<Long> doOnChanged(PurchaseOrder purchaseOrder) {
        return map.fastRemove(purchaseOrder.getOrderId());
    }

    @Scheduled(fixedRate = 1_000)
    public void updateCache(){
        repo.findAll()
                .collectList()
                .map(list -> list.stream().collect(Collectors.toMap(PurchaseOrder::getOrderId, Function.identity())))
                .flatMap(map::putAll)
                .subscribe();
    }

    public Flux<PurchaseOrder> findAll(){
        return map.readAllValues()
                .as(Flux::from)
                .flatMap(Flux::fromIterable);
    }
}
