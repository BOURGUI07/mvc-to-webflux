package com.example.order_service.cache;

import static com.example.order_service.util.Constants.RedisKeys.ORDER_KEY;

import com.example.order_service.entity.PurchaseOrder;
import com.example.order_service.repo.PurchaseOrderRepo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderCacheService implements CacheService<PurchaseOrder> {
    private final PurchaseOrderRepo repo;
    private final ReactiveHashOperations<String, UUID, PurchaseOrder> operations;

    @Scheduled(cron = "0 0 0 * * *")
    public void evictCache() {
        operations
                .keys(ORDER_KEY)
                .flatMap(key -> operations.remove(ORDER_KEY, key))
                .subscribe();
    }

    @Override
    public Mono<PurchaseOrder> findById(UUID id) {
        return operations
                .get(ORDER_KEY, id)
                .switchIfEmpty(repo.findByOrderId(id)
                        .flatMap(order -> operations.put(ORDER_KEY, id, order).thenReturn(order)));
    }

    @Override
    public Mono<Long> doOnChanged(PurchaseOrder purchaseOrder) {
        return operations.remove(ORDER_KEY, purchaseOrder.getOrderId());
    }

    @Scheduled(fixedRate = 1_000)
    public void updateCache() {
        repo.findAll()
                .flatMap(order -> operations.putIfAbsent(ORDER_KEY, order.getOrderId(), order))
                .subscribe();
    }

    public Flux<PurchaseOrder> findAll() {
        return operations.values(ORDER_KEY);
    }
}
