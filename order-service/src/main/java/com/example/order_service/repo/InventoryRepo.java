package com.example.order_service.repo;

import com.example.order_service.entity.OrderInventory;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface InventoryRepo extends ReactiveCrudRepository<OrderInventory, Long> {
    Mono<OrderInventory> findByOrderId(UUID orderId);
}
