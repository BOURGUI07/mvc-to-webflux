package com.example.order_service.listener;

import com.example.order_service.events.InventoryEvent;
import com.example.order_service.events.OrderEvent;
import reactor.core.publisher.Mono;

public interface InventoryEventListener extends EventListener<InventoryEvent> {
    @Override
    default Mono<Void> listen(InventoryEvent inventoryEvent) {
        return switch (inventoryEvent){
            case InventoryEvent.Declined e -> handle(e);
            case InventoryEvent.Deducted e -> handle(e);
            case InventoryEvent.Restored e -> handle(e);
        };
    }

    Mono<Void> handle(InventoryEvent.Deducted inventoryEvent);
    Mono<Void> handle(InventoryEvent.Declined inventoryEvent);
    Mono<Void> handle(InventoryEvent.Restored inventoryEvent);

}
