package com.example.order_service.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.order_service.dto.OrderDTO;
import com.example.order_service.enums.InventoryStatus;
import com.example.order_service.enums.PaymentStatus;
import com.example.order_service.enums.ShippingStatus;
import com.example.order_service.events.InventoryEvent;
import com.example.order_service.events.PaymentEvent;
import com.example.order_service.events.ShippingEvent;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

public class EventProcessorTests extends AbstractEventProcessorTests {

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void test() throws InterruptedException {
        // when all events(payment, shipping, inventory) react positively to CreatedOrderEvent
        // then OrderEventCompleted
        // in other words if PaymentDeducted AND InventoryDeducted AND ShippingReady then OrderEventCompleted

        // initiate order
        var request = OrderDTO.Request.builder()
                .customerId(1L)
                .productId(1L)
                .quantity(4)
                .build();

        var orderId = initiateOrder(request);

        // verify OrderCreated

        verifyOrderEventCreated(orderId, event -> {
            assertEquals(1L, event.customerId());
            assertEquals(1L, event.productId());
            assertEquals(4, event.quantity());
        });

        // emit PaymentEventDeducted

        var paymentEvent = PaymentEvent.Deducted.builder()
                .paymentId(UUID.randomUUID())
                .customerId(1L)
                .deductedAmount(new BigDecimal(400))
                .orderId(orderId)
                .build();

        emitPaymentEvent(paymentEvent);

        // emit InventoryEventDeducted

        var inventoryEvent = InventoryEvent.Deducted.builder()
                .inventoryId(UUID.randomUUID())
                .productId(1L)
                .price(BigDecimal.valueOf(100))
                .orderId(orderId)
                .deductedQty(4)
                .build();

        emitInventoryEvent(inventoryEvent);

        // emit ShippingEventReady

        var shippingEvent = ShippingEvent.Ready.builder()
                .orderId(orderId)
                .shippingId(UUID.randomUUID())
                .build();

        emitShippingEvent(shippingEvent);

        Thread.sleep(15_000);

        // verify OrderCompleted

        verifyOrderEventCompleted(orderId);

        // verify OrderDetails

        verifyOrderDetails(orderId, detail -> {
            assertEquals(InventoryStatus.DEDUCTED, detail.inventory().status());
            assertEquals(PaymentStatus.DEDUCTED, detail.payment().status());
            assertEquals(ShippingStatus.PENDING, detail.shipping().status());
        });
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void test1() throws InterruptedException {
        // whenInventoryDeclined Then No matter what status of both payment/shipping services
        // the order will be nonetheless cancelled

        // initiate order
        var request = OrderDTO.Request.builder()
                .customerId(1L)
                .productId(1L)
                .quantity(4)
                .build();

        var orderId = initiateOrder(request);

        // verify OrderCreated

        verifyOrderEventCreated(orderId, event -> {
            assertEquals(1L, event.customerId());
            assertEquals(1L, event.productId());
            assertEquals(4, event.quantity());
        });

        // emit InventoryEventDeclined

        var inventoryEvent = InventoryEvent.Declined.builder()
                .productId(1L)
                .orderId(orderId)
                .reason("Not enough inventory")
                .build();

        emitInventoryEvent(inventoryEvent);

        // emit PaymentEventDeducted

        var paymentEvent = PaymentEvent.Deducted.builder()
                .paymentId(UUID.randomUUID())
                .customerId(1L)
                .deductedAmount(new BigDecimal(400))
                .orderId(orderId)
                .build();

        emitPaymentEvent(paymentEvent);

        // emit ShippingEventReady

        var shippingEvent = ShippingEvent.Ready.builder()
                .orderId(orderId)
                .shippingId(UUID.randomUUID())
                .build();

        emitShippingEvent(shippingEvent);

        Thread.sleep(15_000);

        // verify OrderCompleted

        verifyOrderEventCancelled(orderId);

        // verify OrderDetails

        verifyOrderDetails(orderId, detail -> {
            assertEquals("Not enough inventory", detail.inventory().message());
            assertEquals(InventoryStatus.DECLINED, detail.inventory().status());
            assertEquals(PaymentStatus.DEDUCTED, detail.payment().status());
            assertEquals(ShippingStatus.PENDING, detail.shipping().status());
        });
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void test2() throws InterruptedException {
        // whenPaymentDeclined Then No matter what status of both inventory/shipping services
        // the order will be nonetheless cancelled

        // initiate order
        var request = OrderDTO.Request.builder()
                .customerId(1L)
                .productId(1L)
                .quantity(4)
                .build();

        var orderId = initiateOrder(request);

        // verify OrderCreated

        verifyOrderEventCreated(orderId, event -> {
            assertEquals(1L, event.customerId());
            assertEquals(1L, event.productId());
            assertEquals(4, event.quantity());
        });

        // emit InventoryEventDeducted

        var inventoryEvent = InventoryEvent.Deducted.builder()
                .productId(1L)
                .orderId(orderId)
                .inventoryId(UUID.randomUUID())
                .deductedQty(4)
                .build();

        emitInventoryEvent(inventoryEvent);

        // emit PaymentEventDeclined

        var paymentEvent = PaymentEvent.Declined.builder()
                .customerId(1L)
                .orderId(orderId)
                .reason("Not enough balance")
                .build();

        emitPaymentEvent(paymentEvent);

        // emit ShippingEventReady

        var shippingEvent = ShippingEvent.Ready.builder()
                .orderId(orderId)
                .shippingId(UUID.randomUUID())
                .build();

        emitShippingEvent(shippingEvent);

        Thread.sleep(15_000);

        // verify OrderCancelled

        verifyOrderEventCancelled(orderId);

        // verify OrderDetails

        verifyOrderDetails(orderId, detail -> {
            assertEquals("Not enough balance", detail.payment().message());
            assertEquals(InventoryStatus.DEDUCTED, detail.inventory().status());
            assertEquals(PaymentStatus.DECLINED, detail.payment().status());
            assertEquals(ShippingStatus.PENDING, detail.shipping().status());
        });
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void test3() throws InterruptedException {
        // whenShippingDeclined Then No matter what status of both payment/inventory services
        // the order will be nonetheless cancelled

        // initiate order
        var request = OrderDTO.Request.builder()
                .customerId(1L)
                .productId(1L)
                .quantity(4)
                .build();

        var orderId = initiateOrder(request);

        // verify OrderCreated

        verifyOrderEventCreated(orderId, event -> {
            assertEquals(1L, event.customerId());
            assertEquals(1L, event.productId());
            assertEquals(4, event.quantity());
        });

        // emit InventoryEventDeducted

        var inventoryEvent = InventoryEvent.Deducted.builder()
                .productId(1L)
                .orderId(orderId)
                .deductedQty(4)
                .inventoryId(UUID.randomUUID())
                .build();

        emitInventoryEvent(inventoryEvent);

        // emit PaymentEventDeducted

        var paymentEvent = PaymentEvent.Deducted.builder()
                .paymentId(UUID.randomUUID())
                .customerId(1L)
                .deductedAmount(new BigDecimal(400))
                .orderId(orderId)
                .build();

        emitPaymentEvent(paymentEvent);

        // emit ShippingEventDeclined

        var shippingEvent = ShippingEvent.Declined.builder()
                .orderId(orderId)
                .reason("Quantity Limit Exceeded")
                .build();

        emitShippingEvent(shippingEvent);

        Thread.sleep(15_000);

        // verify OrderCancelled

        verifyOrderEventCancelled(orderId);

        // verify OrderDetails

        verifyOrderDetails(orderId, detail -> {
            assertEquals("Quantity Limit Exceeded", detail.shipping().message());
            assertEquals(InventoryStatus.DEDUCTED, detail.inventory().status());
            assertEquals(PaymentStatus.DEDUCTED, detail.payment().status());
            assertEquals(ShippingStatus.DECLINED, detail.shipping().status());
        });
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void test4() throws InterruptedException {
        // when all declined then order cancelled

        // initiate order
        var request = OrderDTO.Request.builder()
                .customerId(1L)
                .productId(1L)
                .quantity(4)
                .build();

        var orderId = initiateOrder(request);

        // verify OrderCreated

        verifyOrderEventCreated(orderId, event -> {
            assertEquals(1L, event.customerId());
            assertEquals(1L, event.productId());
            assertEquals(4, event.quantity());
        });

        // emit InventoryEventDeclined

        var inventoryEvent = InventoryEvent.Declined.builder()
                .productId(1L)
                .orderId(orderId)
                .reason("Not enough inventory")
                .build();

        emitInventoryEvent(inventoryEvent);

        // emit PaymentEventDeclined

        var paymentEvent = PaymentEvent.Declined.builder()
                .customerId(1L)
                .orderId(orderId)
                .reason("Not enough balance")
                .build();

        emitPaymentEvent(paymentEvent);

        // emit ShippingEventDeclined

        var shippingEvent = ShippingEvent.Declined.builder()
                .orderId(orderId)
                .reason("Quantity Limit Exceeded")
                .build();

        emitShippingEvent(shippingEvent);

        Thread.sleep(15_000);

        // verify OrderCancelled

        verifyOrderEventCancelled(orderId);

        // verify OrderDetails

        verifyOrderDetails(orderId, detail -> {
            assertEquals("Not enough balance", detail.payment().message());
            assertEquals("Quantity Limit Exceeded", detail.shipping().message());
            assertEquals("Not enough inventory", detail.inventory().message());
            assertEquals(InventoryStatus.DECLINED, detail.inventory().status());
            assertEquals(PaymentStatus.DECLINED, detail.payment().status());
            assertEquals(ShippingStatus.DECLINED, detail.shipping().status());
        });
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void test5() throws InterruptedException {
        // when inventory deducted then restored and both payment/shipping are declined
        // the order is cancelled

        // initiate order
        var request = OrderDTO.Request.builder()
                .customerId(1L)
                .productId(1L)
                .quantity(4)
                .build();

        var orderId = initiateOrder(request);

        // verify OrderCreated

        verifyOrderEventCreated(orderId, event -> {
            assertEquals(1L, event.customerId());
            assertEquals(1L, event.productId());
            assertEquals(4, event.quantity());
        });

        // emit InventoryEventDeclined

        var inventoryEvent = InventoryEvent.Deducted.builder()
                .productId(1L)
                .orderId(orderId)
                .deductedQty(4)
                .inventoryId(UUID.randomUUID())
                .build();

        emitInventoryEvent(inventoryEvent);

        // emit InventoryEventRestored

        var inventoryEvent1 = InventoryEvent.Restored.builder()
                .productId(1L)
                .orderId(orderId)
                .inventoryId(inventoryEvent.inventoryId())
                .restoredQty(4)
                .build();

        emitInventoryEvent(inventoryEvent1);

        // emit PaymentEventDeducted

        var paymentEvent = PaymentEvent.Declined.builder()
                .customerId(1L)
                .orderId(orderId)
                .reason("Not enough balance")
                .build();

        emitPaymentEvent(paymentEvent);

        // emit ShippingEventReady

        var shippingEvent = ShippingEvent.Declined.builder()
                .orderId(orderId)
                .reason("Quantity Limit Exceeded")
                .build();

        emitShippingEvent(shippingEvent);

        Thread.sleep(15_000);

        // verify OrderCancelled

        verifyOrderEventCancelled(orderId);

        // verify OrderDetails

        verifyOrderDetails(orderId, detail -> {
            assertEquals(InventoryStatus.RESTORED, detail.inventory().status());
            assertEquals(PaymentStatus.DECLINED, detail.payment().status());
            assertEquals(ShippingStatus.DECLINED, detail.shipping().status());
        });
    }
}
