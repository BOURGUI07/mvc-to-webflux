package com.example.customer_service.MESSAGING_Tests;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.shaded.org.checkerframework.checker.index.qual.SameLen;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
public class OrderEventProcessorTests extends AbstractOrderEventProcessorTest{

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void test1(){
        var event = TestDataUtils.toCreatedOrderEvent().apply(751L,251L,10);
        whenOrderEventCreatedThenPaymentEventDeducted(event,new BigDecimal(2400));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void test2(){
        var event = TestDataUtils.toCreatedOrderEvent().apply(751L,251L,10);
        whenOrderEventCancelledThenPaymentEventRefunded(event,new BigDecimal(2400));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void test3(){
        var event = TestDataUtils.toCreatedOrderEvent().apply(751L,251L,10);
        whenDuplicateEventThenMonoEmpty(event,new BigDecimal(2400));
    }

    @Test
    void test4(){
        var event = TestDataUtils.toCancelledOrderEvent().apply(UUID.randomUUID());
        whenRefundBeforeDeductThenMonoEmpty(event);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void test5(){
        var event = TestDataUtils.toCreatedOrderEvent().apply(1L,1L,10);
        whenCustomerNotFoundThenPaymentEventDeclined(event);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void test6(){
        var event = TestDataUtils.toCreatedOrderEvent().apply(1L,251L,1000);
        whenNotEnoughBalanceThenPaymentEventDeclined(event);
    }
}
