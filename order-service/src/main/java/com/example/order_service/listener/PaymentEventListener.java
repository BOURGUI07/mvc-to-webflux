package com.example.order_service.listener;

import com.example.order_service.events.PaymentEvent;
import reactor.core.publisher.Mono;

public interface PaymentEventListener extends EventListener<PaymentEvent> {

    @Override
    default Mono<Void> listen(PaymentEvent paymentEvent) {
        return switch (paymentEvent) {
            case PaymentEvent.Declined e -> handle(e);
            case PaymentEvent.Deducted e -> handle(e);
            case PaymentEvent.Refunded e -> handle(e);
        };
    }

    Mono<Void> handle(PaymentEvent.Deducted paymentEvent);

    Mono<Void> handle(PaymentEvent.Declined paymentEvent);

    Mono<Void> handle(PaymentEvent.Refunded paymentEvent);
}
