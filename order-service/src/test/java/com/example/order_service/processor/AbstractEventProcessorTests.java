package com.example.order_service.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.order_service.AbstractIntegrationTests;
import com.example.order_service.dto.OrderDTO;
import com.example.order_service.dto.OrderDetails;
import com.example.order_service.entity.Product;
import com.example.order_service.events.InventoryEvent;
import com.example.order_service.events.OrderEvent;
import com.example.order_service.events.PaymentEvent;
import com.example.order_service.events.ShippingEvent;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

@Import(AbstractEventProcessorTests.testConfiguration.class)
@TestPropertySource(
        properties = {
            "spring.cloud.function.definition=consumer;paymentEventListener;inventoryEventListener;shippingEventListener;orderEventProducer",
            "spring.cloud.stream.bindings.consumer-in-0.destination=order-events"
        })
public class AbstractEventProcessorTests extends AbstractIntegrationTests {
    private static final Sinks.Many<OrderEvent> sink = Sinks.many().unicast().onBackpressureBuffer();
    private static final Flux<OrderEvent> flux = sink.asFlux().cache(0);

    @TestConfiguration
    static class testConfiguration {
        @Bean
        public Consumer<Flux<OrderEvent>> consumer() {
            return flux -> flux.doOnNext(sink::tryEmitNext).subscribe();
        }
    }

    protected void emitPaymentEvent(PaymentEvent paymentEvent) {
        streamBridge.send("customer-events", paymentEvent);
    }

    protected void emitInventoryEvent(InventoryEvent inventoryEvent) {
        streamBridge.send("catalog-events", inventoryEvent);
    }

    protected void emitShippingEvent(ShippingEvent shippingEvent) {
        streamBridge.send("shipping-events", shippingEvent);
    }

    private final Product product = Product.builder()
            .price(new BigDecimal("100.00"))
            .productId(1L)
            .code("p100")
            .build();

    public void insertData() {
        productRepo.save(product).then().as(StepVerifier::create).verifyComplete();
    }

    protected void expectNoEvent() {
        flux.next()
                .timeout(Duration.ofSeconds(10), Mono.empty())
                .as(StepVerifier::create)
                .verifyComplete();
    }

    private <T extends OrderEvent> void expectEvent(Class<T> type, Consumer<T> consumer) {
        flux.next()
                .cast(type)
                .timeout(Duration.ofMillis(timeout))
                .as(StepVerifier::create)
                .assertNext(consumer)
                .verifyComplete();
    }

    protected void verifyOrderEventCreated(UUID orderId, Consumer<OrderEvent.Created> consumer) {
        expectEvent(OrderEvent.Created.class, event -> assertEquals(orderId, event.orderId()));
    }

    protected void verifyOrderEventCompleted(UUID orderId) {
        expectEvent(OrderEvent.Completed.class, event -> assertEquals(orderId, event.orderId()));
    }

    protected void verifyOrderEventCancelled(UUID orderId) {
        expectEvent(OrderEvent.Cancelled.class, event -> assertEquals(orderId, event.orderId()));
    }

    protected UUID initiateOrder(OrderDTO.Request request) {
        insertData();
        var atomicOrderId = new AtomicReference<UUID>();

        client.post()
                .uri("/api/orders")
                .bodyValue(request)
                .exchange()
                .returnResult(OrderDTO.Response.class)
                .getResponseBody()
                .doOnNext(dto -> atomicOrderId.set(dto.orderId()))
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
        return atomicOrderId.get();
    }

    protected void verifyOrderDetails(UUID orderId, Consumer<OrderDetails> consumer) {
        client.get()
                .uri("/api/orders/{orderId}", orderId)
                .exchange()
                .returnResult(OrderDetails.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .assertNext(consumer)
                .verifyComplete();
    }
}
