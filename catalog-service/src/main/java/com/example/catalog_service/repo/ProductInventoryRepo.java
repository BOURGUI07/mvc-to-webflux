package com.example.catalog_service.repo;

import com.example.catalog_service.domain.InventoryStatus;
import com.example.catalog_service.domain.ProductInventory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ProductInventoryRepo extends ReactiveCrudRepository<ProductInventory, UUID> {

    Mono<Boolean> existsByOrderId(UUID orderId);

    Mono<ProductInventory> findByOrderIdAndStatus(UUID orderId, InventoryStatus status);
}
