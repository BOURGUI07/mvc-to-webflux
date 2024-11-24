package com.example.order_service.events;

import reactor.core.publisher.Mono;

public interface ShippingEventProcessor extends EventProcessor<ShippingEvent,OrderEvent> {
    @Override
    default Mono<OrderEvent> process(ShippingEvent shippingEvent) {
        return switch (shippingEvent){
            case ShippingEvent.Declined e -> handle(e);
            case ShippingEvent.Ready e -> handle(e);
            case ShippingEvent.Cancelled e -> handle(e);
            case ShippingEvent.Scheduled e -> handle(e);
        };
    }

    Mono<OrderEvent> handle(ShippingEvent.Ready paymentEvent);
    Mono<OrderEvent> handle(ShippingEvent.Declined paymentEvent);
    Mono<OrderEvent> handle(ShippingEvent.Cancelled paymentEvent);
    Mono<OrderEvent> handle(ShippingEvent.Scheduled paymentEvent);
}