package com.example.order_service.service.cache;

import com.example.order_service.entity.OrderPayment;
import com.example.order_service.entity.OrderShipping;
import com.example.order_service.repo.PaymentRepo;
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
public class PaymentCacheService implements CacheService<OrderPayment> {
    private final PaymentRepo repo;
    private final RedissonReactiveClient redissonClient;
    private RMapReactive<UUID, OrderPayment> map;

    @PostConstruct
    public void initializeMap() {
        this.map = redissonClient.getMap("payment",new TypedJsonJacksonCodec(UUID.class,OrderPayment.class));
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
    public Mono<OrderPayment> findById(UUID id) {
        return map.get(id)
                .switchIfEmpty(repo.findByOrderId(id)
                        .flatMap(payment -> map.fastPut(id,payment).thenReturn(payment))
                );
    }

    @Override
    public Mono<Long> doOnChanged(OrderPayment orderPayment) {
        return map.fastRemove(orderPayment.getOrderId());
    }
}
