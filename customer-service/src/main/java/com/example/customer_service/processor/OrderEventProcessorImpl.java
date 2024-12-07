package com.example.customer_service.processor;

import com.example.customer_service.dto.PaymentDTO;
import com.example.customer_service.events.OrderEvent;
import com.example.customer_service.events.OrderEventProcessor;
import com.example.customer_service.events.PaymentEvent;
import com.example.customer_service.exceptions.CustomerNotFoundException;
import com.example.customer_service.exceptions.DuplicateEventException;
import com.example.customer_service.exceptions.NotEnoughBalanceException;
import com.example.customer_service.mapper.EventMapper;
import com.example.customer_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrderEventProcessorImpl implements OrderEventProcessor {
    private final PaymentService service;

    /**
     * if the OrderEvent received is Created, then it will convert it into
     * PaymentRequest DTO so that the payment-service will be able to process it
     * After processing, the payment-service will return a PaymentResponse.DTO
     * the latter will be converted into DeductedPaymentEvent.
     * If a duplicate event exception is raised, then do nothing
     * A DeclinedPaymentEvent will be returned if either CustomerNotFoundException or NotEnoughBalanceException is raised
     */

    @Override
    public Mono<PaymentEvent> handle(OrderEvent.Created event) {
        return service.processPayment().apply(((PaymentDTO.Request) EventMapper.toRequest().apply(event)))
                .cast(PaymentDTO.Response.class)
                .map(EventMapper.toDeducted())
                .doOnError(DuplicateEventException.class, ex -> log.error("DUPLICATED EVENT"))
                .onErrorResume(DuplicateEventException.class, ex ->Mono.empty())
                .onErrorResume(CustomerNotFoundException.class, ex -> EventMapper.toDeclined().apply(ex,event))
                .onErrorResume(NotEnoughBalanceException.class, ex -> EventMapper.toDeclined().apply(ex,event));
    }

    /**
     * If the order is completed, we basically have to do nothing
     */

    @Override
    public Mono<PaymentEvent> handle(OrderEvent.Completed event) {
        return Mono.empty();
    }


    /**
     * If the order is cancelled, we basically have to refund the payment.
     * we will only need event orderId to process the refund request.
     * The payment-service will return a PaymentResponseDTO
     * will converted to RefundedPaymentEvent
     */
    @Override
    public Mono<PaymentEvent> handle(OrderEvent.Cancelled event) {
        return service.refund().apply(event.orderId())
                .cast(PaymentDTO.Response.class)
                .map(EventMapper.toRefunded());

    }
}
