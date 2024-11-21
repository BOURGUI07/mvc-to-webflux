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

    @Override
    public Mono<PaymentEvent> handle(OrderEvent.Completed event) {
        return Mono.empty();
    }

    @Override
    public Mono<PaymentEvent> handle(OrderEvent.Cancelled event) {
        return service.refund().apply(event.orderId())
                .cast(PaymentDTO.Response.class)
                .map(EventMapper.toRefunded());

    }
}
