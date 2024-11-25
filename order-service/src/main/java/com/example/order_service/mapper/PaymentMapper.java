package com.example.order_service.mapper;

import com.example.order_service.dto.OrderInventoryDTO;
import com.example.order_service.dto.OrderPaymentDTO;
import com.example.order_service.entity.OrderInventory;
import com.example.order_service.entity.OrderPayment;
import com.example.order_service.enums.InventoryStatus;
import com.example.order_service.enums.PaymentStatus;
import com.example.order_service.events.InventoryEvent;
import com.example.order_service.events.PaymentEvent;

import java.util.function.Function;

public class PaymentMapper {

    public static Function<PaymentEvent.Deducted, OrderPaymentDTO> fromDeductedToPaymentDTO() {
        return event -> OrderPaymentDTO.builder()
                .paymentId(event.paymentId())
                .status(PaymentStatus.DEDUCTED)
                .orderId(event.orderId())
                .build();
    }

    public static Function<PaymentEvent.Refunded, OrderPaymentDTO> fromRefundedToPaymentDTO() {
        return event -> OrderPaymentDTO.builder()
                .paymentId(event.paymentId())
                .status(PaymentStatus.REFUNDED)
                .orderId(event.orderId())
                .build();
    }

    public static Function<PaymentEvent.Declined, OrderPaymentDTO> fromDeclinedToPaymentDTO() {
        return event -> OrderPaymentDTO.builder()
                .status(PaymentStatus.DECLINED)
                .orderId(event.orderId())
                .message(event.reason())
                .build();
    }

    public static Function<OrderPayment,OrderPaymentDTO> toDTO(){
        return entity -> OrderPaymentDTO.builder()
                .paymentId(entity.getPaymentId())
                .orderId(entity.getOrderId())
                .status(entity.getStatus())
                .message(entity.getMessage())
                .build();
    }

    public static Function<OrderPaymentDTO,OrderPayment> toEntity(){
        return dto -> OrderPayment.builder()
                .orderId(dto.orderId())
                .status(dto.status())
                .message(dto.message())
                .paymentId(dto.paymentId())
                .build();
    }




}
