package com.example.customer_service.events;

import reactor.core.publisher.Mono;

public interface OrderEventProcessor extends EventProcessor<OrderEvent,PaymentEvent> {

    @Override
    default Mono<PaymentEvent> process(OrderEvent orderEvent) {
        return switch (orderEvent){
            case OrderEvent.Cancelled e -> handle(e);
            case OrderEvent.Completed e -> handle(e);
            case OrderEvent.Created e -> handle(e);
        };
    }

    Mono<PaymentEvent> handle(OrderEvent.Created event);
    Mono<PaymentEvent> handle(OrderEvent.Completed event);
    Mono<PaymentEvent> handle(OrderEvent.Cancelled event);
}
