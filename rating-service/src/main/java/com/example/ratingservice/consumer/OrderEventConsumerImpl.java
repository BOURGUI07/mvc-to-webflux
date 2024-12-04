package com.example.ratingservice.consumer;

import com.example.ratingservice.events.OrderEvent;
import com.example.ratingservice.exceptions.DuplicateEventException;
import com.example.ratingservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumerImpl implements OrderEventConsumer {
    private final OrderService orderService;

    @Override
    public Mono<Void> handle(OrderEvent.Completed event) {
        return orderService
                .saveOrder(event)
                .doOnError(DuplicateEventException.class, ex -> log.info("DUPLICATE EVENT"))
                .onErrorResume(DuplicateEventException.class, ex -> Mono.empty());
    }
}
