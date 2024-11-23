package com.example.notification_service.repo;

import com.example.notification_service.entity.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepo extends ReactiveCrudRepository<Customer, Long> {

    Mono<Customer> findByCustomerId(Long customerId);

    Mono<Boolean> existsByCustomerId(Long customerId);
}
