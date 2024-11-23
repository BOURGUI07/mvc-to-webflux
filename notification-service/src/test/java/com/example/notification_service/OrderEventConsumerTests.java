package com.example.notification_service;

import com.example.notification_service.entity.Customer;
import com.example.notification_service.entity.OrderStatus;
import com.example.notification_service.events.OrderEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(OutputCaptureExtension.class)
public class OrderEventConsumerTests extends AbstractIntegrationTests{

    @BeforeEach
    void setUp() {
        repo.save(Customer.builder()
                        .username("testUsername")
                        .email("testEmail@gmail.com")
                        .customerId(1L)
                .build())
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    private void whenOrderEventPublishedOrderIdSaved(OrderEvent orderEvent) {
        streamBridge.send("order-events",orderEvent);

        Mono.delay(Duration.ofSeconds(2))
                .then(orderRepo.existsByOrderId(orderEvent.orderId()))
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }


    private void duplicateEvent(OrderEvent.Created orderEvent, CapturedOutput output) throws InterruptedException {
        streamBridge.send("order-events",orderEvent);

        Thread.sleep(1000);


        streamBridge.send("order-events",orderEvent);

        Thread.sleep(1000);

        assertTrue(output.getOut().contains("DUPLICATE EVENT"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenCreatedThenIdSaved(){
        var created = OrderEvent.Created.builder()
                .orderId(UUID.randomUUID())
                .customerId(1L)
                .price(new BigDecimal("10"))
                .productId(1L)
                .quantity(45)
                .build();

        whenOrderEventPublishedOrderIdSaved(created);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void duplicateCreated(CapturedOutput output) throws InterruptedException {
        var created = OrderEvent.Created.builder()
                .orderId(UUID.randomUUID())
                .customerId(1L)
                .price(new BigDecimal("10"))
                .productId(1L)
                .quantity(45)
                .build();

        duplicateEvent(created, output);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenOrderEventCompletedThenCompletedStatus(){
        var created = OrderEvent.Created.builder()
                .orderId(UUID.randomUUID())
                .customerId(1L)
                .price(new BigDecimal("10"))
                .productId(1L)
                .quantity(45)
                .build();

        whenOrderEventPublishedOrderIdSaved(created);

        var completed = OrderEvent.Completed.builder()
                .orderId(created.orderId())
                .customerId(created.customerId())
                .build();

        whenOrderEventPublishedOrderIdSaved(completed);

        Mono.delay(Duration.ofSeconds(2))
                .then(orderRepo.findById(1L))
                .as(StepVerifier::create)
                .assertNext(o -> assertEquals(OrderStatus.COMPLETED,o.getStatus()))
                .verifyComplete();
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenOrderEventCancelledThenCancelledStatus(){
        var created = OrderEvent.Created.builder()
                .orderId(UUID.randomUUID())
                .customerId(1L)
                .price(new BigDecimal("10"))
                .productId(1L)
                .quantity(45)
                .build();

        whenOrderEventPublishedOrderIdSaved(created);

        var cancelled = OrderEvent.Cancelled.builder()
                .orderId(created.orderId())
                .customerId(created.customerId())
                .build();

        whenOrderEventPublishedOrderIdSaved(cancelled);

        Mono.delay(Duration.ofSeconds(2))
                .then(orderRepo.findById(1L))
                .as(StepVerifier::create)
                .assertNext(o -> assertEquals(OrderStatus.CANCELED,o.getStatus()))
                .verifyComplete();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenCancelledWithoutCreatedThenEmpty(){
        var cancelled = OrderEvent.Cancelled.builder()
                .orderId(UUID.randomUUID())
                .customerId(1L)
                .build();

        streamBridge.send("order-events",cancelled);

        Mono.delay(Duration.ofSeconds(1))
                .then(orderRepo.count())
                .timeout(Duration.ofSeconds(10),Mono.empty())
                .as(StepVerifier::create)
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenCompletedWithoutCreatedThenEmpty(){
        var completed = OrderEvent.Completed.builder()
                .orderId(UUID.randomUUID())
                .customerId(1L)
                .build();

        streamBridge.send("order-events",completed);

        Mono.delay(Duration.ofSeconds(1))
                .then(orderRepo.count())
                .timeout(Duration.ofSeconds(10),Mono.empty())
                .as(StepVerifier::create)
                .expectNext(0L)
                .verifyComplete();
    }







}
