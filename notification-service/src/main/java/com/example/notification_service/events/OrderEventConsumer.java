package com.example.notification_service.events;

import reactor.core.publisher.Mono;

public interface OrderEventConsumer extends EventConsumer<OrderEvent> {

    @Override
    default Mono<Void> consume(OrderEvent orderEvent) {
        return switch (orderEvent){
            case OrderEvent.Cancelled e -> handle(e);
            case OrderEvent.Created e -> handle(e);
            case OrderEvent.Completed e -> handle(e);
        };
    }


    Mono<Void> handle(OrderEvent.Cancelled event);
    Mono<Void> handle(OrderEvent.Completed event);
    Mono<Void> handle(OrderEvent.Created event);
}
