package com.example.order_service.processor;

import com.example.order_service.events.OrderEvent;
import com.example.order_service.events.ShippingEvent;
import com.example.order_service.events.ShippingEventProcessor;
import com.example.order_service.mapper.OrderMapper;
import com.example.order_service.mapper.ShippingMapper;
import com.example.order_service.service.OrderFulfillmentService;
import com.example.order_service.service.ShippingService;
import com.example.order_service.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class ShippingEventProcessorImpl implements ShippingEventProcessor {
    private final OrderFulfillmentService orderFulfillmentService;
    private final ShippingService shippingService;

    @Override
    public Mono<OrderEvent> handle(ShippingEvent.Ready shippingEvent) {
        return shippingService.handleReadyShipping(ShippingMapper.fromReadyToShippingDTO().apply(shippingEvent))
                .then(orderFulfillmentService.completeOrder(shippingEvent.orderId()))
                .map(OrderMapper.toCompletedOrderEvent())
                .doOnNext(event -> log.info("The Shipping Processor Produced Order Event: {}", Util.write(event)))
                .doFirst(() -> log.info("The Shipping Processor Received Shipping Event: {}", Util.write(shippingEvent)));
    }

    @Override
    public Mono<OrderEvent> handle(ShippingEvent.Declined shippingEvent) {
        return shippingService.handleFailedShipping(ShippingMapper.fromDeclinedToShippingDTO().apply(shippingEvent))
                .then(orderFulfillmentService.cancelOrder(shippingEvent.orderId()))
                .map(OrderMapper.toCancelledOrderEvent())
                .doOnNext(event -> log.info("The Shipping Processor Produced Order Event: {}", Util.write(event)))
                .doFirst(() -> log.info("The Shipping Processor Received Shipping Event: {}", Util.write(shippingEvent)));
    }

    @Override
    public Mono<OrderEvent> handle(ShippingEvent.Cancelled shippingEvent) {
        return shippingService.handleCancelledShipping(ShippingMapper.fromCancelledToShippingDTO().apply(shippingEvent))
                .then(Mono.empty());
    }

    @Override
    public Mono<OrderEvent> handle(ShippingEvent.Scheduled shippingEvent) {
        return shippingService.handleScheduledShipping(ShippingMapper.fromScheduledToShippingDTO().apply(shippingEvent))
                .then(Mono.empty());
    }
}
