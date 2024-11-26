package com.example.order_service.publisher;

import com.example.order_service.events.OrderEvent;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OrderEventOutboxService extends EventPublisher<OrderEvent> {


    Mono<Void> deleteEvents(List<Long> correlationIds);
}
