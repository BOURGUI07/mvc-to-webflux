package com.example.customer_service.repo;

import com.example.customer_service.domain.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepo extends ReactiveCrudRepository<Customer, Long> {

    Mono<Boolean> existsByUsername(String username);
    Mono<Boolean> existsByEmail(String email);

    Flux<Customer> findBy(Pageable pageable);
}
