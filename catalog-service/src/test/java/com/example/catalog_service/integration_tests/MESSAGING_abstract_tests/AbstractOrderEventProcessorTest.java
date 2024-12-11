package com.example.catalog_service.integration_tests.MESSAGING_abstract_tests;

import com.example.catalog_service.integration_tests.AbstractIntegrationTest;
import com.example.catalog_service.integration_tests.MESSAGING_tests.TestDataUtil;
import com.example.catalog_service.domain.InventoryStatus;
import com.example.catalog_service.events.InventoryEvent;
import com.example.catalog_service.events.OrderEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Import(AbstractOrderEventProcessorTest.testConfig.class)
@TestPropertySource(properties = {
        "spring.cloud.function.definition=processor;consumer",
        "spring.cloud.stream.bindings.consumer-in-0.destination=catalog-events"
})
public abstract class AbstractOrderEventProcessorTest extends AbstractIntegrationTest {
    private final static Sinks.Many<InventoryEvent> sink = Sinks.many().unicast().onBackpressureBuffer();
    private final static Flux<InventoryEvent> resFlux = sink.asFlux().cache(0);

    @TestConfiguration
    static class testConfig{
        @Bean
        public Consumer<Flux<InventoryEvent>> consumer(){
            return flux -> flux
                    .doOnNext(sink::tryEmitNext)
                    .subscribe();
        }
    }

    protected UUID whenOrderEventCreatedThenInventoryEventDeducted(OrderEvent.Created orderEvent, Integer newQuantity){
        var orderId = new AtomicReference<UUID>();
        var inventoryId = new AtomicReference<UUID>();
        var productId = new AtomicLong();
        resFlux
                .doFirst(() ->streamBridge.send("order-events", orderEvent))
                .next()
                .timeout(Duration.ofMillis(timeout))
                .cast(InventoryEvent.Deducted.class)
                .doOnNext(x -> {
                    orderId.set(x.orderId());
                    inventoryId.set(x.inventoryId());
                    productId.set(x.productId());
                })
                .as(StepVerifier::create)
                .assertNext( response -> {
                    assertNotNull(response.inventoryId());
                    assertEquals(orderId.get(), response.orderId());
                    assertEquals(orderEvent.quantity(), response.deductedQty());
                    assertEquals(orderEvent.productId(), response.productId());
                })
                .verifyComplete();

        inventoryRepo.findById(inventoryId.get())
                .as(StepVerifier::create)
                .assertNext(inv -> assertEquals(InventoryStatus.DEDUCTED,inv.getStatus()))
                .verifyComplete();

        productRepo.findById(productId.get())
                .as(StepVerifier::create)
                .assertNext(product -> assertEquals(newQuantity,product.getAvailableQuantity()))
                .verifyComplete();

        return orderId.get();
    }

    protected void whenOrderEventCancelledThenInventoryEventRestored(OrderEvent.Created orderEvent, Integer newQuantity){
        var orderId = whenOrderEventCreatedThenInventoryEventDeducted(orderEvent, newQuantity);

        var cancelledEvent =  TestDataUtil.toCancelledOrderEvent().apply(orderId);

        resFlux
                .doFirst(() ->streamBridge.send("order-events", cancelledEvent))
                .next()
                .timeout(Duration.ofMillis(timeout))
                .cast(InventoryEvent.Restored.class)
                .as(StepVerifier::create)
                .assertNext( response -> {
                    assertNotNull(response.inventoryId());
                    assertEquals(orderId, response.orderId());
                    assertEquals(response.restoredQty(), orderEvent.quantity());
                })
                .verifyComplete();
    }

    protected void expectNoEvent(OrderEvent orderEvent){
        resFlux
                .doFirst(() ->streamBridge.send("order-events", orderEvent))
                .next()
                .timeout(Duration.ofSeconds(15), Mono.empty())
                .as(StepVerifier::create)
                .verifyComplete();
    }

    protected <T> void  expectEvent(OrderEvent orderEvent, Class<T> type, Consumer<T> consumer){
        resFlux
                .doFirst(() ->streamBridge.send("order-events", orderEvent))
                .next()
                .timeout(Duration.ofMillis(timeout))
                .cast(type)
                .as(StepVerifier::create)
                .assertNext(consumer)
                .verifyComplete();
    }

    protected void whenDuplicateEventThenMonoEmpty(OrderEvent.Created orderEvent, Integer newQuantity){
        var orderId = whenOrderEventCreatedThenInventoryEventDeducted(orderEvent, newQuantity);
        var createdEvent = OrderEvent.Created.builder().orderId(orderId)
                .customerId(orderEvent.customerId())
                .productId(orderEvent.productId())
                .quantity(newQuantity).build();


        expectNoEvent(createdEvent);
    }

    protected void whenRestoreBeforeDeductThenMonoEmpty(OrderEvent.Cancelled event){
        expectNoEvent(event);
    }


    protected void whenProductNotFoundThenInventoryEventDeclined(OrderEvent.Created orderEvent){
        expectEvent(orderEvent, InventoryEvent.Declined.class, response -> {
            assertEquals(String.format("Product with id %s not found",orderEvent.productId()),response.reason());
        });
    }

    protected void whenNotEnoughInventoryThenInventoryEventDeclined(OrderEvent.Created orderEvent){
        expectEvent(orderEvent, InventoryEvent.Declined.class, response -> {
            assertEquals(String.format("Product with id %s doesn't have enough inventory",orderEvent.productId()),response.reason());
        });
    }





}
