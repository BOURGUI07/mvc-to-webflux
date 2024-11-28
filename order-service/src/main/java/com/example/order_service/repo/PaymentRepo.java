package com.example.order_service.repo;

import com.example.order_service.entity.OrderPayment;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PaymentRepo extends ReactiveCrudRepository<OrderPayment, Long> {
    Mono<OrderPayment> findByOrderId(UUID orderId);
}
