package com.example.ratingservice.repo;

import com.example.ratingservice.entity.OrderHistory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface OrderHistoryRepo extends ReactiveCrudRepository<OrderHistory, Long> {

    Mono<Boolean> existsByOrderId(UUID orderId);

    Mono<OrderHistory> findByOrderId(UUID orderId);
}
