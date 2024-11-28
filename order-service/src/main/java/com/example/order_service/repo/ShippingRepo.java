package com.example.order_service.repo;

import com.example.order_service.entity.OrderShipping;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ShippingRepo extends ReactiveCrudRepository<OrderShipping, Long> {
    Mono<OrderShipping> findByOrderId(UUID orderId);
}
