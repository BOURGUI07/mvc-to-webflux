package com.example.order_service.mapper;

import com.example.order_service.dto.*;
import com.example.order_service.entity.Product;
import com.example.order_service.entity.PurchaseOrder;
import com.example.order_service.enums.OrderStatus;
import com.example.order_service.events.OrderEvent;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Function;

public class OrderMapper {

    public static BiFunction<OrderDTO.Request, Product, PurchaseOrder> toEntity(){
        return (request, product) -> PurchaseOrder.builder()
                .customerId(request.customerId())
                .price(product.getPrice())
                .amount(product.getPrice().multiply(new BigDecimal(request.quantity())))
                .status(OrderStatus.CREATED)
                .productId(product.getId())
                .quantity(request.quantity())
                .build();
    }

    public static Function<PurchaseOrder,OrderDTO.Response> toDto(){
        return entity -> OrderDTO.Response.builder()
                .amount(entity.getAmount())
                .customerId(entity.getCustomerId())
                .status(entity.getStatus())
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .orderId(entity.getOrderId())
                .productId(entity.getProductId())
                .build();
    }


    public static Function<OrderDTO.Response, OrderEvent> toCreatedOrderEvent(){
        return response -> OrderEvent.Created.builder()
                .customerId(response.customerId())
                .orderId(response.orderId())
                .productId(response.productId())
                .quantity(response.quantity())
                .price(response.price())
                .build();
    }

    public static Function<OrderDTO.Response, OrderEvent> toCancelledOrderEvent(){
        return response -> OrderEvent.Cancelled.builder()
                .customerId(response.customerId())
                .orderId(response.orderId())
                .build();
    }

    public static Function<OrderDTO.Response, OrderEvent> toCompletedOrderEvent(){
        return response -> OrderEvent.Completed.builder()
                .customerId(response.customerId())
                .orderId(response.orderId())
                .build();
    }

    public static OrderDetails toOrderDetails(OrderDTO.Response order, OrderShippingDTO shipping , OrderInventoryDTO inventory, OrderPaymentDTO payment){
        return OrderDetails.builder()
                .order(order)
                .shipping(shipping)
                .inventory(inventory)
                .payment(payment)
                .build();
    }


}
