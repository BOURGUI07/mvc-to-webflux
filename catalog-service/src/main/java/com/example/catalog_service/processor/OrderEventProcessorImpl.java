package com.example.catalog_service.processor;

import com.example.catalog_service.dto.PurchaseDTO;
import com.example.catalog_service.events.InventoryEvent;
import com.example.catalog_service.events.OrderEvent;
import com.example.catalog_service.events.OrderEventProcessor;
import com.example.catalog_service.exceptions.DuplicatedEventException;
import com.example.catalog_service.exceptions.NotEnoughInventoryException;
import com.example.catalog_service.exceptions.ProductNotFoundException;
import com.example.catalog_service.mapper.Mapper;
import com.example.catalog_service.service.InventoryService;
import com.example.catalog_service.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProcessorImpl implements OrderEventProcessor {
    private final InventoryService service;

    @Override
    public Mono<InventoryEvent> handle(OrderEvent.Created event) {
        return service.processRequest(Mapper.toPurchaseRequest().apply(event))
                .map( response ->Mapper.toDeducted().apply(((PurchaseDTO.Response) response)))
                .doOnError(DuplicatedEventException.class, ex -> log.info("DUPLICATED EVENT!"))
                .onErrorResume(DuplicatedEventException.class, ex ->Mono.empty())
                .onErrorResume(ProductNotFoundException.class, ex -> Mapper.toDeclined().apply(ex,event))
                .onErrorResume(NotEnoughInventoryException.class, ex -> Mapper.toDeclined().apply(ex,event))
                .doFirst(() -> log.info("OrderEventProcessorImpl Received OrderEvent.Created: {}", Util.write(event)))
                .doOnNext(inventoryEvent -> log.info("OrderEventProcessorImpl Processing Result: {}", Util.write(inventoryEvent)));
    }

    @Override
    public Mono<InventoryEvent> handle(OrderEvent.Completed event) {
        return Mono.<InventoryEvent>empty()
                .doFirst(() -> log.info("OrderEventProcessorImpl Received OrderEvent.Completed: {}",Util.write(event)));
    }

    @Override
    public Mono<InventoryEvent> handle(OrderEvent.Cancelled event) {
        return service.restore(event.orderId())
                .map(response ->Mapper.toRestored().apply((PurchaseDTO.Response) response))
                .doFirst(() -> log.info("OrderEventProcessorImpl Received OrderEvent.Cancelled: {}",Util.write(event)))
                .doOnNext(inventoryEvent -> log.info("OrderEventProcessorImpl Processing Result: {}", Util.write(inventoryEvent)));
    }




}
