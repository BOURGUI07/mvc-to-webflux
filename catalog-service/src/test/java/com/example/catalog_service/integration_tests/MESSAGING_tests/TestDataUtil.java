package com.example.catalog_service.integration_tests.MESSAGING_tests;

import com.example.catalog_service.events.OrderEvent;
import org.apache.commons.lang3.function.TriFunction;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class TestDataUtil {

    public static TriFunction<Long,Long,Integer, OrderEvent.Created> toCreatedOrderEvent(){
        return (productId, customerId, quantity) -> OrderEvent.Created.builder()
                .productId(productId)
                .customerId(customerId)
                .quantity(quantity)
                .orderId(UUID.randomUUID())
                .price(BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(15, 100)))
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
