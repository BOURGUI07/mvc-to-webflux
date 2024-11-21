package com.example.catalog_service.events;

import reactor.core.publisher.Mono;

public interface OrderEventProcessor extends EventProcessor<OrderEvent,InventoryEvent> {

    @Override
    default Mono<InventoryEvent> process(OrderEvent orderEvent) {
        return switch (orderEvent){
          case OrderEvent.Cancelled e -> this.handle(e);
          case OrderEvent.Completed e -> this.handle(e);
          case OrderEvent.Created e -> this.handle(e);
        };
    }

    Mono<InventoryEvent> handle(OrderEvent.Created event);
    Mono<InventoryEvent> handle(OrderEvent.Completed event);
    Mono<InventoryEvent> handle(OrderEvent.Cancelled event);
}
