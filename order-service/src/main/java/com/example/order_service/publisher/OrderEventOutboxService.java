package com.example.order_service.publisher;

import com.example.order_service.events.OrderEvent;
import java.util.List;
import reactor.core.publisher.Mono;

public interface OrderEventOutboxService extends EventPublisher<OrderEvent> {

    Mono<Void> deleteEvents(List<Long> correlationIds);
}
