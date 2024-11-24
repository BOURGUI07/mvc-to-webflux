package com.example.order_service.events;

import reactor.core.publisher.Mono;

public interface InventoryEventProcessor extends EventProcessor<InventoryEvent,OrderEvent> {
    @Override
    default Mono<OrderEvent> process(InventoryEvent inventoryEvent) {
        return switch (inventoryEvent){
            case InventoryEvent.Declined e -> handle(e);
            case InventoryEvent.Deducted e -> handle(e);
            case InventoryEvent.Restored e -> handle(e);
        };
    }

    Mono<OrderEvent> handle(InventoryEvent.Deducted paymentEvent);
    Mono<OrderEvent> handle(InventoryEvent.Declined paymentEvent);
    Mono<OrderEvent> handle(InventoryEvent.Restored paymentEvent);

}
