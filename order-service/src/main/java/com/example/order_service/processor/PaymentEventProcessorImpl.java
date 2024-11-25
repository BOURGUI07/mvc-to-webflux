package com.example.order_service.processor;

import com.example.order_service.events.OrderEvent;
import com.example.order_service.events.PaymentEvent;
import com.example.order_service.events.PaymentEventProcessor;
import com.example.order_service.mapper.OrderMapper;
import com.example.order_service.mapper.PaymentMapper;
import com.example.order_service.service.OrderFulfillmentService;
import com.example.order_service.service.PaymentService;
import com.example.order_service.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProcessorImpl implements PaymentEventProcessor {
    private final PaymentService paymentService;
    private final OrderFulfillmentService orderFulfillmentService;

    @Override
    public Mono<OrderEvent> handle(PaymentEvent.Deducted paymentEvent) {
        return paymentService.handleSuccessfulPayment(PaymentMapper.fromDeductedToPaymentDTO().apply(paymentEvent))
                .then(orderFulfillmentService.completeOrder(paymentEvent.orderId()))
                .map(OrderMapper.toCompletedOrderEvent())
                .doOnNext(event -> log.info("The Payment Processor Produced Order Event: {}", Util.write(event)))
                .doFirst(() -> log.info("The Payment Processor Received Payment Event: {}", Util.write(paymentEvent)));

    }

    @Override
    public Mono<OrderEvent> handle(PaymentEvent.Declined paymentEvent) {
        return paymentService.handleFailedPayment(PaymentMapper.fromDeclinedToPaymentDTO().apply(paymentEvent))
                .then(orderFulfillmentService.cancelOrder(paymentEvent.orderId()))
                .map(OrderMapper.toCancelledOrderEvent())
                .doOnNext(event -> log.info("The Payment Processor Produced Order Event: {}", Util.write(event)))
                .doFirst(() -> log.info("The Payment Processor Received Payment Event: {}", Util.write(paymentEvent)));
    }

    @Override
    public Mono<OrderEvent> handle(PaymentEvent.Refunded paymentEvent) {
        return paymentService.handleRolledBackPayment(PaymentMapper.fromRefundedToPaymentDTO().apply(paymentEvent))
                .then(Mono.empty());
    }
}
