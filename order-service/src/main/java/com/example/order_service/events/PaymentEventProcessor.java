package com.example.order_service.events;

import reactor.core.publisher.Mono;

public interface PaymentEventProcessor extends EventProcessor<PaymentEvent,OrderEvent> {

    @Override
    default Mono<OrderEvent> process(PaymentEvent paymentEvent) {
        return switch (paymentEvent){
          case PaymentEvent.Declined e -> handle(e);
            case PaymentEvent.Deducted e -> handle(e);
            case PaymentEvent.Refunded e -> handle(e);
        };
    }

    Mono<OrderEvent> handle(PaymentEvent.Deducted paymentEvent);
    Mono<OrderEvent> handle(PaymentEvent.Declined paymentEvent);
    Mono<OrderEvent> handle(PaymentEvent.Refunded paymentEvent);
}
