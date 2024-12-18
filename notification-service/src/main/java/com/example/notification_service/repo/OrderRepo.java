package com.example.notification_service.repo;

import com.example.notification_service.entity.Order;
import com.example.notification_service.entity.OrderStatus;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface OrderRepo extends ReactiveCrudRepository<Order, Long> {

    Mono<Boolean> existsByOrderId(UUID orderId);

    Mono<Order> findByOrderIdAndStatus(UUID orderId, OrderStatus status);
}
