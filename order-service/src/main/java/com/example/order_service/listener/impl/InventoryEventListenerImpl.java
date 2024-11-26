package com.example.order_service.listener.impl;

import com.example.order_service.events.InventoryEvent;
import com.example.order_service.listener.InventoryEventListener;
import com.example.order_service.mapper.InventoryMapper;
import com.example.order_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListenerImpl implements InventoryEventListener {
    private final InventoryService inventoryService;

    @Override
    public Mono<Void> handle(InventoryEvent.Deducted inventoryEvent) {
        return inventoryService.handleSuccessfulInventory(InventoryMapper.fromDeductedToInventoryDTO().apply(inventoryEvent));
    }

    @Override
    public Mono<Void> handle(InventoryEvent.Declined inventoryEvent) {
        return inventoryService.handleFailedInventory(InventoryMapper.fromDeclinedToInventoryDTO().apply(inventoryEvent));
    }

    @Override
    public Mono<Void> handle(InventoryEvent.Restored inventoryEvent) {
        return inventoryService.handleRolledBackInventory(InventoryMapper.fromRestoredToInventoryDTO().apply(inventoryEvent));
    }
}
