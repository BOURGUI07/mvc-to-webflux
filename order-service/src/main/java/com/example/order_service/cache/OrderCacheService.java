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
    private final ReactiveHashOperations<String, String, PurchaseOrder> operations;

    @Scheduled(cron = "0 0 0 * * *")
    public void evictCache() {
        operations
                .delete(ORDER_KEY)
                .doOnError(ex -> log.error("Failed to evict cache: {}", ex.getMessage()))
                .subscribe();
    }

    @Override
    public Mono<PurchaseOrder> findById(UUID id) {
        return operations
                .get(ORDER_KEY, id.toString())
                .switchIfEmpty(repo.findByOrderId(id)
                        .flatMap(order -> operations.put(ORDER_KEY, id.toString(), order).thenReturn(order)));
    }

    @Override
    public Mono<Long> doOnChanged(PurchaseOrder purchaseOrder) {
        return operations.remove(ORDER_KEY, purchaseOrder.getOrderId().toString());
    }

    @Scheduled(fixedRate = 1_000)
    public void updateCache() {
        repo.findAll()
                .flatMap(order -> operations.putIfAbsent(ORDER_KEY, order.getOrderId().toString(), order))
                .subscribe();
    }

    public Flux<PurchaseOrder> findAll() {
        return operations.values(ORDER_KEY);
    }
}
