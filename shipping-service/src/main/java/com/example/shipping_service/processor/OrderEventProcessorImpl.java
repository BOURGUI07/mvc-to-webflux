package com.example.shipping_service.processor;

import com.example.shipping_service.dto.ShippingDTO;
import com.example.shipping_service.events.OrderEvent;
import com.example.shipping_service.events.OrderEventProcessor;
import com.example.shipping_service.events.ShippingEvent;
import com.example.shipping_service.exception.DuplicateEventException;
import com.example.shipping_service.exception.QuantityLimitException;
import com.example.shipping_service.mapper.Mapper;
import com.example.shipping_service.service.ShippingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProcessorImpl implements OrderEventProcessor {

    private final ShippingService service;

    @Override
    public Mono<ShippingEvent> handle(OrderEvent.Created event) {
        return service.planShipping().apply(((ShippingDTO.Request) Mapper.toRequest().apply(event)))
                .map(dto ->Mapper.toReady().apply(((ShippingDTO.Response) dto)))
                .doOnError(DuplicateEventException.class, ex -> log.warn("DUPLICATE EVENT"))
                .onErrorResume(DuplicateEventException.class, ex -> Mono.empty())
                .onErrorResume(QuantityLimitException.class, ex -> Mapper.toDeclined().apply(ex, event));
    }

    @Override
    public Mono<ShippingEvent> handle(OrderEvent.Completed event) {
        return service.scheduleShipping().apply(event.orderId())
                .cast(ShippingDTO.Response.class)
                .map(Mapper.toScheduled());
    }

    @Override
    public Mono<ShippingEvent> handle(OrderEvent.Cancelled event) {
        return service.cancelShipping().apply(event.orderId())
                .cast(ShippingDTO.Response.class)
                .map(Mapper.toCancelled());
    }
}
