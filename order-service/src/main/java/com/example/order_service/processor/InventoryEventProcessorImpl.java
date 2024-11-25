package com.example.order_service.processor;

import com.example.order_service.events.InventoryEvent;
import com.example.order_service.events.InventoryEventProcessor;
import com.example.order_service.events.OrderEvent;
import com.example.order_service.mapper.InventoryMapper;
import com.example.order_service.mapper.OrderMapper;
import com.example.order_service.service.InventoryService;
import com.example.order_service.service.OrderFulfillmentService;
import com.example.order_service.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryEventProcessorImpl implements InventoryEventProcessor {

    private final InventoryService inventoryService;
    private final OrderFulfillmentService orderFulfillmentService;


    @Override
    public Mono<OrderEvent> handle(InventoryEvent.Deducted inventoryEvent) {
        return inventoryService.handleSuccessfulInventory(InventoryMapper.fromDeductedToInventoryDTO().apply(inventoryEvent))
                .then(orderFulfillmentService.completeOrder(inventoryEvent.orderId()))
                .map(OrderMapper.toCompletedOrderEvent())
                .doOnNext(event -> log.info("The Inventory Processor Produced Order Event: {}", Util.write(event)))
                .doFirst(() -> log.info("The Inventory Processor Received Inventory Event: {}", Util.write(inventoryEvent)));
    }

    @Override
    public Mono<OrderEvent> handle(InventoryEvent.Declined inventoryEvent) {
        return inventoryService.handleFailedInventory(InventoryMapper.fromDeclinedToInventoryDTO().apply(inventoryEvent))
                .then(orderFulfillmentService.cancelOrder(inventoryEvent.orderId()))
                .map(OrderMapper.toCancelledOrderEvent())
                .doOnNext(event -> log.info("The Inventory Processor Produced Order Event: {}", Util.write(event)))
                .doFirst(() -> log.info("The Inventory Processor Received Inventory Event: {}", Util.write(inventoryEvent)));
    }

    @Override
    public Mono<OrderEvent> handle(InventoryEvent.Restored inventoryEvent) {
        return inventoryService.handleRolledBackInventory(InventoryMapper.fromRestoredToInventoryDTO().apply(inventoryEvent))
                .then(Mono.empty());
    }


}
