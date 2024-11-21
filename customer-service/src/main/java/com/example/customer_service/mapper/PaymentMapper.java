package com.example.customer_service.mapper;

import com.example.customer_service.domain.CustomerPayment;
import com.example.customer_service.dto.PaymentDTO;

import java.util.function.Function;

public class PaymentMapper {

    public static Function<PaymentDTO.Request, CustomerPayment> toEntity(){
        return request -> CustomerPayment.builder()
                .amount(request.amount())
                .customerId(request.customerId())
                .orderId(request.orderId())
                .build();
    }

    public static Function<CustomerPayment,PaymentDTO> toDTO(){
        return entity -> PaymentDTO.Response.builder()
                .amount(entity.getAmount())
                .customerId(entity.getCustomerId())
                .orderId(entity.getOrderId())
                .paymentId(entity.getPaymentId())
                .status(entity.getStatus())
                .build();
    }
}
