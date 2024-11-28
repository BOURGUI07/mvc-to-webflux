package com.example.order_service.repo;

import com.example.order_service.entity.PurchaseOrder;
import com.example.order_service.enums.OrderStatus;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PurchaseOrderRepo extends ReactiveCrudRepository<PurchaseOrder, UUID> {

    Mono<PurchaseOrder> findByOrderIdAndStatus(UUID orderId, OrderStatus status);

    Mono<PurchaseOrder> findByOrderId(UUID orderId);
}
