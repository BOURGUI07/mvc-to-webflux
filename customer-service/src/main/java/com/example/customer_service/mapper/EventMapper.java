package com.example.customer_service.mapper;

import com.example.customer_service.dto.CustomerDTO;
import com.example.customer_service.dto.PaymentDTO;
import com.example.customer_service.events.CustomerEvent;
import com.example.customer_service.events.OrderEvent;
import com.example.customer_service.events.PaymentEvent;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;
import java.util.function.Function;

public class EventMapper {

    public static Function<PaymentDTO.Response, PaymentEvent> toDeducted(){
        return dto -> PaymentEvent.Deducted.builder()
                .deductedAmount(dto.amount())
                .orderId(dto.orderId())
                .paymentId(dto.paymentId())
                .customerId(dto.customerId())
                .build();
    }

    public static Function<PaymentDTO.Response, PaymentEvent> toRefunded(){
        return dto -> PaymentEvent.Refunded.builder()
                .refundedAmount(dto.amount())
                .orderId(dto.orderId())
                .paymentId(dto.paymentId())
                .customerId(dto.customerId())
                .build();
    }

    public static BiFunction<Throwable, OrderEvent.Created, Mono<PaymentEvent>> toDeclined(){
        return (ex,event) -> Mono.fromSupplier(() -> PaymentEvent.Declined.builder()
                .customerId(event.customerId())
                .orderId(event.orderId())
                .reason(ex.getMessage())
                .build());
    }

    public static Function<OrderEvent.Created,PaymentDTO> toRequest(){
        return event -> PaymentDTO.Request.builder()
                .amount(event.amount())
                .orderId(event.orderId())
                .customerId(event.customerId())
                .build();
    }


    public static Function<CustomerDTO.Response, CustomerEvent> toCustomerEvent(){
        return dto -> CustomerEvent.Created.builder()
                .customerId(dto.customerId())
                .email(dto.email())
                .username(dto.username())
                .build();
    }
}
