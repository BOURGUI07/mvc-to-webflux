package com.example.catalog_service.MESSAGING_tests;

import com.example.catalog_service.MESSAGING_abstract_tests.AbstractMessagingTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.UUID;

public class MessagingTests extends AbstractMessagingTest {

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void test1(){
        var event = TestDataUtil.toCreatedOrderEvent().apply(751L,1L,10);
        whenOrderEventCreatedThenInventoryEventDeducted(event,40);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void test2(){
        var event = TestDataUtil.toCreatedOrderEvent().apply(751L,1L,10);
        whenOrderEventCancelledThenInventoryEventRestored(event,40);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void test3(){
        var event = TestDataUtil.toCreatedOrderEvent().apply(751L,1L,10);
        whenDuplicateEventThenMonoEmpty(event,40);
    }

    @Test
    void test4(){
        var event = TestDataUtil.toCancelledOrderEvent().apply(UUID.randomUUID());
        whenRestoreBeforeDeductThenMonoEmpty(event);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void test5(){
        var event = TestDataUtil.toCreatedOrderEvent().apply(1L,1L,10);
        whenProductNotFoundThenInventoryEventDeclined(event);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void test6(){
        var event = TestDataUtil.toCreatedOrderEvent().apply(751L,1L,1000);
        whenNotEnoughInventoryThenInventoryEventDeclined(event);
    }

    @Test
    void test7(){
        var event = TestDataUtil.toPriceCalculatedEvent(751L, 1L,20,new BigDecimal("17.5"));
        whenOrderEventPriceCalculatedThenMonoEmpty(event);
    }

}
