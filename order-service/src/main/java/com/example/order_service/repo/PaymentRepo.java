package com.example.order_service.repo;

import com.example.order_service.entity.OrderInventory;
import com.example.order_service.entity.OrderPayment;
import com.example.order_service.enums.InventoryStatus;
import com.example.order_service.enums.PaymentStatus;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PaymentRepo extends ReactiveCrudRepository<OrderPayment,Long> {
    Mono<OrderPayment> findByOrderIdAndStatus(UUID orderId, PaymentStatus status);
}
