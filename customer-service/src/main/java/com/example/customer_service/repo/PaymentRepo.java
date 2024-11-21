package com.example.customer_service.repo;

import com.example.customer_service.domain.CustomerPayment;
import com.example.customer_service.domain.PaymentStatus;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PaymentRepo extends ReactiveCrudRepository<CustomerPayment, UUID> {

    Mono<CustomerPayment> findByOrderIdAndStatus(UUID orderId, PaymentStatus status);
    Mono<Boolean> existsByOrderId(UUID orderId);
}
