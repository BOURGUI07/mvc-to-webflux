package com.example.shipping_service.events;

import reactor.core.publisher.Mono;

public interface OrderEventProcessor extends EventProcessor<OrderEvent, ShippingEvent> {

    @Override
    default Mono<ShippingEvent> process(OrderEvent orderEvent) {
        return switch (orderEvent){
            case OrderEvent.Cancelled e -> handle(e);
            case OrderEvent.Completed e -> handle(e);
            case OrderEvent.Created e -> handle(e);
        };
    }

    Mono<ShippingEvent> handle(OrderEvent.Created event);
    Mono<ShippingEvent> handle(OrderEvent.Completed event);
    Mono<ShippingEvent> handle(OrderEvent.Cancelled event);
}
