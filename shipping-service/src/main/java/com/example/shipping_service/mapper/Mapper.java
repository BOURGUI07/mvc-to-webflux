package com.example.shipping_service.mapper;


import com.example.shipping_service.domain.Shipment;
import com.example.shipping_service.dto.ShippingDTO;
import com.example.shipping_service.events.OrderEvent;
import com.example.shipping_service.events.ShippingEvent;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Mapper {

    public static Function<OrderEvent.Created, ShippingDTO> toRequest(){
        return event -> ShippingDTO.Request.builder()
                .quantity(event.quantity())
                .productId(event.productId())
                .customerId(event.customerId())
                .orderId(event.orderId())
                .build();
    }

    public static Function<ShippingDTO.Request, Shipment> toEntity(){
        return request -> Shipment.builder()
                .customerId(request.customerId())
                .productId(request.productId())
                .quantity(request.quantity())
                .orderId(request.orderId())
                .build();
    }

    public static Function<Shipment,ShippingDTO> toDto(){
        return entity -> ShippingDTO.Response.builder()
                .deliveryDate(entity.getDeliveryDate())
                .status(entity.getStatus())
                .shippingId(entity.getShippingId())
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .orderId(entity.getOrderId())
                .customerId(entity.getCustomerId())
                .build();
    }

    public static Function<ShippingDTO.Response, ShippingEvent> toReady(){
        return response -> ShippingEvent.Ready.builder()
                .shippingId(response.shippingId())
                .orderId(response.orderId())
                .build();
    }

    public static Function<ShippingDTO.Response, ShippingEvent> toCancelled(){
        return response -> ShippingEvent.Cancelled.builder()
                .orderId(response.orderId())
                .shippingId(response.shippingId())
                .build();
    }

    public static Function<ShippingDTO.Response, ShippingEvent> toScheduled(){
        return response  -> ShippingEvent.Scheduled.builder()
                .shippingId(response.orderId())
                .deliveryDate(response.deliveryDate())
                .orderId(response.orderId())
                .build();
    }

    public static BiFunction<Throwable, OrderEvent.Created, Mono<ShippingEvent>> toDeclined(){
        return (ex,event) -> Mono.fromSupplier(() -> ShippingEvent.Declined.builder()
                .orderId(event.orderId())
                .reason(ex.getMessage())
                .build());
    }
}
