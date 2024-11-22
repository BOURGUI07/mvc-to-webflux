package com.example.shipping_service;

import com.example.shipping_service.domain.ShippingStatus;
import com.example.shipping_service.events.OrderEvent;
import com.example.shipping_service.events.ShippingEvent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Import(OrderEventProcessorTests.testConfig.class)
@TestPropertySource(properties = {
        "spring.cloud.function.definition=processor;consumer",
        "spring.cloud.stream.bindings.consumer-in-0.destination=shipping-events"
})
public class OrderEventProcessorTests extends AbstractIntegrationTests {
    private final static Sinks.Many<ShippingEvent> sink = Sinks.many().unicast().onBackpressureBuffer();
    private final static Flux<ShippingEvent> resFlux = sink.asFlux().cache(0);

    @TestConfiguration
    static class testConfig{
        @Bean
        public Consumer<Flux<ShippingEvent>> consumer(){
            return flux -> flux
                    .doOnNext(sink::tryEmitNext)
                    .subscribe();
        }
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenOrderEventCreatedThenShippingEventReady(){
        var createdEvent = OrderEvent.Created.builder()
                .orderId(UUID.randomUUID())
                .customerId(1L)
                .quantity(9)
                .productId(1L)
                .price(new BigDecimal(15))
                .build();

        var orderId = createdEvent.orderId();

        resFlux
                .doFirst(() -> streamBridge.send("order-events", createdEvent))
                .next()
                .timeout(Duration.ofSeconds(10))
                .cast(ShippingEvent.Ready.class)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(orderId,response.orderId());
                    assertNotNull(response.shippingId());
                })
                .verifyComplete();

        repo.findByOrderId(orderId)
                .as(StepVerifier::create)
                .assertNext(x -> assertEquals(ShippingStatus.PENDING,x.getStatus()))
                .verifyComplete();

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenDuplicateEventThenEmpty(){
        var createdEvent = OrderEvent.Created.builder()
                .orderId(UUID.randomUUID())
                .customerId(1L)
                .quantity(9)
                .productId(1L)
                .price(new BigDecimal(15))
                .build();

        var orderId = createdEvent.orderId();

        resFlux
                .doFirst(() -> streamBridge.send("order-events", createdEvent))
                .next()
                .timeout(Duration.ofSeconds(10))
                .cast(ShippingEvent.Ready.class)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(orderId,response.orderId());
                    assertNotNull(response.shippingId());
                })
                .verifyComplete();

        repo.findByOrderId(orderId)
                .as(StepVerifier::create)
                .assertNext(x -> assertEquals(ShippingStatus.PENDING,x.getStatus()))
                .verifyComplete();

        resFlux
                .doFirst(() -> streamBridge.send("order-events", createdEvent))
                .next()
                .timeout(Duration.ofSeconds(10), Mono.empty())
                .as(StepVerifier::create)
                .verifyComplete();

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenQuantityLimitExceededThenShippingEventDeclined(){
        var createdEvent = OrderEvent.Created.builder()
                .orderId(UUID.randomUUID())
                .customerId(1L)
                .quantity(11)
                .productId(1L)
                .price(new BigDecimal(15))
                .build();

        var orderId = createdEvent.orderId();

        resFlux
                .doFirst(() -> streamBridge.send("order-events", createdEvent))
                .next()
                .timeout(Duration.ofSeconds(10))
                .cast(ShippingEvent.Declined.class)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(orderId,response.orderId());
                    assertNotNull(String.format("Quantity of order with id %s exceeded the limit",orderId),response.reason());
                })
                .verifyComplete();
    }

    @Test
    void whenScheduleBeforePlaningShippingThenEmpty(){
        var completedOrder = OrderEvent.Completed.builder()
                .orderId(UUID.randomUUID())
                .build();
        resFlux
                .doFirst(() -> streamBridge.send("order-events", completedOrder))
                .next()
                .timeout(Duration.ofSeconds(10),Mono.empty())
                .as(StepVerifier::create)
                .verifyComplete();

    }


    @Test
    void whenCancelBeforePlaningShippingThenEmpty(){
        var cancelledOrder = OrderEvent.Cancelled.builder()
                .orderId(UUID.randomUUID())
                .build();
        resFlux
                .doFirst(() -> streamBridge.send("order-events", cancelledOrder))
                .next()
                .timeout(Duration.ofSeconds(10),Mono.empty())
                .as(StepVerifier::create)
                .verifyComplete();

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenOrderEventCancelledThenShippingEventCancelled(){
        var createdEvent = OrderEvent.Created.builder()
                .orderId(UUID.randomUUID())
                .customerId(1L)
                .quantity(9)
                .productId(1L)
                .price(new BigDecimal(15))
                .build();

        var orderId = createdEvent.orderId();

        resFlux
                .doFirst(() -> streamBridge.send("order-events", createdEvent))
                .next()
                .timeout(Duration.ofSeconds(10))
                .cast(ShippingEvent.Ready.class)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(orderId,response.orderId());
                    assertNotNull(response.shippingId());
                })
                .verifyComplete();

        repo.findByOrderId(orderId)
                .as(StepVerifier::create)
                .assertNext(x -> assertEquals(ShippingStatus.PENDING,x.getStatus()))
                .verifyComplete();


        var cancelledEvent = OrderEvent.Cancelled.builder()
                .orderId(orderId)
                .build();

        resFlux
                .doFirst(() -> streamBridge.send("order-events", cancelledEvent))
                .next()
                .timeout(Duration.ofSeconds(10))
                .cast(ShippingEvent.Cancelled.class)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(orderId,response.orderId());
                    assertNotNull(response.shippingId());
                })
                .verifyComplete();

        repo.findByOrderId(orderId)
                .as(StepVerifier::create)
                .assertNext(x -> assertEquals(ShippingStatus.CANCELLED,x.getStatus()))
                .verifyComplete();

    }



    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenOrderEventCompletedThenShippingEventScheduled(){
        var createdEvent = OrderEvent.Created.builder()
                .orderId(UUID.randomUUID())
                .customerId(1L)
                .quantity(9)
                .productId(1L)
                .price(new BigDecimal(15))
                .build();

        var orderId = createdEvent.orderId();

        resFlux
                .doFirst(() -> streamBridge.send("order-events", createdEvent))
                .next()
                .timeout(Duration.ofSeconds(10))
                .cast(ShippingEvent.Ready.class)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(orderId,response.orderId());
                    assertNotNull(response.shippingId());
                })
                .verifyComplete();

        repo.findByOrderId(orderId)
                .as(StepVerifier::create)
                .assertNext(x -> assertEquals(ShippingStatus.PENDING,x.getStatus()))
                .verifyComplete();


        var completedEvent = OrderEvent.Completed.builder()
                .orderId(orderId)
                .build();

        resFlux
                .doFirst(() -> streamBridge.send("order-events", completedEvent))
                .next()
                .timeout(Duration.ofSeconds(10))
                .cast(ShippingEvent.Scheduled.class)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(orderId,response.orderId());
                    assertNotNull(response.shippingId());
                })
                .verifyComplete();

        repo.findByOrderId(orderId)
                .as(StepVerifier::create)
                .assertNext(x -> assertEquals(ShippingStatus.SCHEDULED,x.getStatus()))
                .verifyComplete();

    }







}



