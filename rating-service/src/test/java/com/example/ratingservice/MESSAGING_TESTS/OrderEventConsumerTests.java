package com.example.ratingservice.MESSAGING_TESTS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.ratingservice.AbstractIntegrationTests;
import com.example.ratingservice.events.OrderEvent;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

@TestPropertySource(
        properties = {
            "spring.cloud.function.definition=completedOrderEventConsumer",
            "spring.cloud.stream.bindings.completedOrderEventConsumer-in-0.destination=order-events"
        })
@ExtendWith(OutputCaptureExtension.class)
public class OrderEventConsumerTests extends AbstractIntegrationTests {

    private OrderEvent.Completed createOrderEvent(Long customerId, Long productId) {
        return OrderEvent.Completed.builder()
                .orderId(UUID.randomUUID())
                .customerId(customerId)
                .productId(productId)
                .build();
    }

    @Test
    void whenOrderEventCompletedThenOrderHistorySaved() throws InterruptedException {
        var event = createOrderEvent(1L, 1L);
        streamBridge.send("order-events", event);

        Thread.sleep(Duration.ofSeconds(timeout));

        orderRepo
                .findByOrderId(event.orderId())
                .as(StepVerifier::create)
                .assertNext(orderHistory -> {
                    assertEquals(orderHistory.getCustomerId(), event.customerId());
                    assertEquals(orderHistory.getProductId(), event.productId());
                    assertNotNull(orderHistory.getId());
                })
                .verifyComplete();
    }

    @Test
    void whenDuplicatedOrderEventThenMonoEmpty(CapturedOutput output) throws InterruptedException {
        var event = createOrderEvent(1L, 1L);
        streamBridge.send("order-events", event);

        Thread.sleep(Duration.ofSeconds(timeout));

        orderRepo
                .findByOrderId(event.orderId())
                .as(StepVerifier::create)
                .assertNext(orderHistory -> {
                    assertEquals(orderHistory.getCustomerId(), event.customerId());
                    assertEquals(orderHistory.getProductId(), event.productId());
                    assertNotNull(orderHistory.getId());
                })
                .verifyComplete();

        streamBridge.send("order-events", event);

        Thread.sleep(Duration.ofSeconds(timeout));

        assert output.getOut().contains("DUPLICATE EVENT");
    }
}
