package com.example.customer_service.MESSAGING_Tests;

import com.example.customer_service.events.OrderEvent;
import org.apache.commons.lang3.function.TriFunction;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Function;

public class TestDataUtils {
    public static TriFunction<Long,Long,Integer, OrderEvent.Created> toCreatedOrderEvent(){
        return (productId, customerId, quantity) -> OrderEvent.Created.builder()
                .productId(productId)
                .customerId(customerId)
                .quantity(quantity)
                .orderId(UUID.randomUUID())
                .price(new BigDecimal(10))
                .build();
    }


    public static Function<UUID,OrderEvent.Completed> toCompletedOrderEvent(){
        return orderId -> OrderEvent.Completed.builder()
                .orderId(orderId)
                .build();
    }

    public static Function<UUID,OrderEvent.Cancelled> toCancelledOrderEvent(){
        return orderId -> OrderEvent.Cancelled.builder()
                .orderId(orderId)
                .build();
    }
}
