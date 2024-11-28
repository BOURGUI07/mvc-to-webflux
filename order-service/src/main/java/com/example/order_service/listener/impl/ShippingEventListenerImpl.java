package com.example.order_service.listener.impl;

import com.example.order_service.events.ShippingEvent;
import com.example.order_service.listener.ShippingEventListener;
import com.example.order_service.mapper.ShippingMapper;
import com.example.order_service.service.ShippingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingEventListenerImpl implements ShippingEventListener {
    private final ShippingService service;

    @Override
    public Mono<Void> handle(ShippingEvent.Ready shippingEvent) {
        return service.handleReadyShipping(
                ShippingMapper.fromReadyToShippingDTO().apply(shippingEvent));
    }

    @Override
    public Mono<Void> handle(ShippingEvent.Declined shippingEvent) {
        return service.handleFailedShipping(
                ShippingMapper.fromDeclinedToShippingDTO().apply(shippingEvent));
    }

    @Override
    public Mono<Void> handle(ShippingEvent.Cancelled shippingEvent) {
        return service.handleCancelledShipping(
                ShippingMapper.fromCancelledToShippingDTO().apply(shippingEvent));
    }

    @Override
    public Mono<Void> handle(ShippingEvent.Scheduled shippingEvent) {
        return service.handleScheduledShipping(
                ShippingMapper.fromScheduledToShippingDTO().apply(shippingEvent));
    }
}
