package com.example.order_service.listener.impl;

import com.example.order_service.events.PaymentEvent;
import com.example.order_service.listener.PaymentEventListener;
import com.example.order_service.mapper.PaymentMapper;
import com.example.order_service.service.OrderService;
import com.example.order_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListenerImpl implements PaymentEventListener {
    private final PaymentService service;

    @Override
    public Mono<Void> handle(PaymentEvent.Deducted paymentEvent) {
        return service.handleSuccessfulPayment(PaymentMapper.fromDeductedToPaymentDTO().apply(paymentEvent));
    }

    @Override
    public Mono<Void> handle(PaymentEvent.Declined paymentEvent) {
        return service.handleFailedPayment(PaymentMapper.fromDeclinedToPaymentDTO().apply(paymentEvent));
    }

    @Override
    public Mono<Void> handle(PaymentEvent.Refunded paymentEvent) {
        return service.handleRolledBackPayment(PaymentMapper.fromRefundedToPaymentDTO().apply(paymentEvent));
    }
}
