package com.example.order_service.publisher;

import com.example.order_service.events.OrderSaga;
import com.example.order_service.outbox.OutboxDTO;
import reactor.core.publisher.Flux;

public interface EventPublisher<T extends OrderSaga> {

    Flux<OutboxDTO<T>> publish();
}
