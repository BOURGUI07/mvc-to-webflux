package com.example.order_service.outbox;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface OrderOutboxRepo extends ReactiveCrudRepository<OrderOutbox, Long> {

    Flux<OrderOutbox> findAllByOrderById();
}
