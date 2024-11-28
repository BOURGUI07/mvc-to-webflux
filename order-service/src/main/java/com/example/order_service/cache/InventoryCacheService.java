package com.example.order_service.cache;

import static com.example.order_service.util.Constants.RedisKeys.INVENTORY_KEY;

import com.example.order_service.entity.OrderInventory;
import com.example.order_service.repo.InventoryRepo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class InventoryCacheService implements CacheService<OrderInventory> {

    private final InventoryRepo repo;

    private final ReactiveHashOperations<String, UUID, OrderInventory> operations;

    @Scheduled(cron = "0 0 0 * * *")
    public void evictCache() {
        operations.delete(INVENTORY_KEY).subscribe();
    }

    @Override
    public Mono<OrderInventory> findById(UUID id) {
        return operations
                .get(INVENTORY_KEY, id)
                .switchIfEmpty(repo.findByOrderId(id)
                        .flatMap(inventory ->
                                operations.put(INVENTORY_KEY, id, inventory).thenReturn(inventory)));
    }

    @Override
    public Mono<Long> doOnChanged(OrderInventory inventory) {
        return operations.remove(INVENTORY_KEY, inventory.getOrderId());
    }
}
