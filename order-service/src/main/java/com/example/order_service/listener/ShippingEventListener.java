package com.example.order_service.listener;

import com.example.order_service.events.OrderEvent;
import com.example.order_service.events.ShippingEvent;
import reactor.core.publisher.Mono;

public interface ShippingEventListener extends EventListener<ShippingEvent> {
    @Override
    default Mono<Void> listen(ShippingEvent shippingEvent) {
        return switch (shippingEvent){
            case ShippingEvent.Declined e -> handle(e);
            case ShippingEvent.Ready e -> handle(e);
            case ShippingEvent.Cancelled e -> handle(e);
            case ShippingEvent.Scheduled e -> handle(e);
        };
    }

    Mono<Void> handle(ShippingEvent.Ready shippingEvent);
    Mono<Void> handle(ShippingEvent.Declined shippingEvent);
    Mono<Void> handle(ShippingEvent.Cancelled shippingEvent);
    Mono<Void> handle(ShippingEvent.Scheduled shippingEvent);
}
