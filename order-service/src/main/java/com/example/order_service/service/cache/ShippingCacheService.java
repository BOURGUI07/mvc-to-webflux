package com.example.order_service.service.cache;

import com.example.order_service.entity.OrderShipping;
import com.example.order_service.repo.PurchaseOrderRepo;
import com.example.order_service.repo.ShippingRepo;
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
public class ShippingCacheService implements CacheService<OrderShipping> {
    private final ShippingRepo repo;
    private final RedissonReactiveClient redissonClient;
    private RMapReactive<UUID, OrderShipping> map;

    @PostConstruct
    public void initializeMap() {
        this.map = redissonClient.getMap("shipping",new TypedJsonJacksonCodec(UUID.class,OrderShipping.class));
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
    public Mono<OrderShipping> findById(UUID orderId) {
        return map.get(orderId)
                .switchIfEmpty(repo.findByOrderId(orderId)
                        .flatMap(shipping -> map.fastPut(orderId,shipping).thenReturn(shipping))
                );
    }

    @Override
    public Mono<Long> doOnChanged(OrderShipping shipping) {
        return map.fastRemove(shipping.getOrderId());
    }
}
