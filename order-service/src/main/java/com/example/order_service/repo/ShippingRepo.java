package com.example.order_service.repo;

import com.example.order_service.entity.OrderInventory;
import com.example.order_service.entity.OrderShipping;
import com.example.order_service.enums.InventoryStatus;
import com.example.order_service.enums.ShippingStatus;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ShippingRepo extends ReactiveCrudRepository<OrderShipping, Long> {
    Mono<OrderShipping> findByOrderId(UUID orderId);
}
