package com.example.order_service.cache;

import static com.example.order_service.util.Constants.RedisKeys.SHIPPING_KEY;

import com.example.order_service.entity.OrderShipping;
import com.example.order_service.repo.ShippingRepo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ShippingCacheService implements CacheService<OrderShipping> {
    private final ShippingRepo repo;
    private final ReactiveHashOperations<String, UUID, OrderShipping> operations;

    @Scheduled(cron = "0 0 0 * * *")
    public void evictCache() {
        operations.delete(SHIPPING_KEY).subscribe();
    }

    @Override
    public Mono<OrderShipping> findById(UUID id) {
        return operations
                .get(SHIPPING_KEY, id)
                .switchIfEmpty(repo.findByOrderId(id)
                        .flatMap(
                                shipping -> operations.put(SHIPPING_KEY, id, shipping).thenReturn(shipping)));
    }

    @Override
    public Mono<Long> doOnChanged(OrderShipping shipping) {
        return operations.remove(SHIPPING_KEY, shipping.getOrderId());
    }
}