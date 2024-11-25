package com.example.order_service.repo;

import com.example.order_service.entity.OrderShipping;
import com.example.order_service.entity.PurchaseOrder;
import com.example.order_service.enums.OrderStatus;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PurchaseOrderRepo extends ReactiveCrudRepository<PurchaseOrder, UUID> {

    Mono<PurchaseOrder> findByOrderIdAndStatus(UUID orderId, OrderStatus status);
    Mono<PurchaseOrder> findByOrderId(UUID orderId);
}
