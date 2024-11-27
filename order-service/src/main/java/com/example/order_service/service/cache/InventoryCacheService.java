package com.example.order_service.service.cache;

import com.example.order_service.entity.OrderInventory;
import com.example.order_service.entity.OrderPayment;
import com.example.order_service.repo.InventoryRepo;
import com.example.order_service.repo.PaymentRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryCacheService implements CacheService<OrderInventory> {
    private final InventoryRepo repo;
    private final RedissonReactiveClient redissonClient;
    private RMapReactive<UUID, OrderInventory> map;

    @PostConstruct
    public void initializeMap() {
        this.map = redissonClient.getMap("inventory",new TypedJsonJacksonCodec(UUID.class,OrderInventory.class));
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
    public Mono<OrderInventory> findById(UUID id) {
        return map.get(id)
                .switchIfEmpty(repo.findByOrderId(id)
                        .flatMap(inventory -> map.fastPut(id,inventory).thenReturn(inventory))
                );
    }

    @Override
    public Mono<Long> doOnChanged(OrderInventory inventory) {
        return map.fastRemove(inventory.getOrderId());
    }
}
