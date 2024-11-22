package com.example.customer_service.MESSAGING_Tests;

import com.example.customer_service.AbstractIntegrationTests;
import com.example.customer_service.domain.PaymentStatus;
import com.example.customer_service.events.OrderEvent;
import com.example.customer_service.events.PaymentEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
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
        "spring.cloud.stream.bindings.consumer-in-0.destination=customer-events"
})
public abstract class AbstractOrderEventProcessorTest extends AbstractIntegrationTests {
    private final static Sinks.Many<PaymentEvent> sink = Sinks.many().unicast().onBackpressureBuffer();
    private final static Flux<PaymentEvent> resFlux = sink.asFlux().cache(0);

    @TestConfiguration
    static class testConfig{
        @Bean
        public Consumer<Flux<PaymentEvent>> consumer(){
            return flux -> flux
                    .doOnNext(sink::tryEmitNext)
                    .subscribe();
        }
    }


    protected UUID whenOrderEventCreatedThenPaymentEventDeducted(OrderEvent.Created orderEvent, BigDecimal newBalance){
        var orderId = new AtomicReference<UUID>();
        var paymentId = new AtomicReference<UUID>();
        var customerId = new AtomicLong();
        resFlux
                .doFirst(() ->streamBridge.send("order-events", orderEvent))
                .next()
                .timeout(Duration.ofSeconds(10))
                .cast(PaymentEvent.Deducted.class)
                .doOnNext(x -> {
                    orderId.set(x.orderId());
                    paymentId.set(x.paymentId());
                    customerId.set(x.customerId());
                })
                .as(StepVerifier::create)
                .assertNext( response -> {
                    assertNotNull(response.paymentId());
                    assertEquals(orderId.get(), response.orderId());
                    assertEquals(orderEvent.amount(), response.deductedAmount());
                    assertEquals(orderEvent.customerId(), response.customerId());
                })
                .verifyComplete();

        repo.findById(paymentId.get())
                .as(StepVerifier::create)
                .assertNext(inv -> assertEquals(PaymentStatus.DEDUCTED,inv.getStatus()))
                .verifyComplete();

        customerRepo.findById(customerId.get())
                .as(StepVerifier::create)
                .assertNext(customer -> assertEquals(newBalance,customer.getBalance()))
                .verifyComplete();

        return orderId.get();
    }

    protected void whenOrderEventCancelledThenPaymentEventRefunded(OrderEvent.Created orderEvent, BigDecimal newBalance){
        var orderId = whenOrderEventCreatedThenPaymentEventDeducted(orderEvent, newBalance);

        var cancelledEvent =  TestDataUtils.toCancelledOrderEvent().apply(orderId);

        resFlux
                .doFirst(() ->streamBridge.send("order-events", cancelledEvent))
                .next()
                .timeout(Duration.ofSeconds(10))
                .cast(PaymentEvent.Refunded.class)
                .as(StepVerifier::create)
                .assertNext( response -> {
                    assertNotNull(response.paymentId());
                    assertEquals(orderId, response.orderId());
                    assertEquals(response.refundedAmount(), orderEvent.amount());
                })
                .verifyComplete();
    }

    protected void expectNoEvent(OrderEvent orderEvent){
        resFlux
                .doFirst(() ->streamBridge.send("order-events", orderEvent))
                .next()
                .timeout(Duration.ofSeconds(10), Mono.empty())
                .as(StepVerifier::create)
                .verifyComplete();
    }

    protected <T> void  expectEvent(OrderEvent orderEvent, Class<T> type, Consumer<T> consumer){
        resFlux
                .doFirst(() ->streamBridge.send("order-events", orderEvent))
                .next()
                .timeout(Duration.ofSeconds(10))
                .cast(type)
                .as(StepVerifier::create)
                .assertNext(consumer)
                .verifyComplete();
    }

    protected void whenDuplicateEventThenMonoEmpty(OrderEvent.Created orderEvent, BigDecimal newBalance){
        var orderId = whenOrderEventCreatedThenPaymentEventDeducted(orderEvent, newBalance);

        var createdEvent = OrderEvent.Created.builder().orderId(orderId)
                .customerId(orderEvent.customerId())
                .productId(orderEvent.productId())
                .price(new BigDecimal(10))
                .quantity(10)
                .build();


        expectNoEvent(createdEvent);
    }

    protected void whenRefundBeforeDeductThenMonoEmpty(OrderEvent.Cancelled event){
        expectNoEvent(event);
    }


    protected void whenCustomerNotFoundThenPaymentEventDeclined(OrderEvent.Created orderEvent){
        expectEvent(orderEvent, PaymentEvent.Declined.class, response -> {
            assertEquals(String.format("Customer with id %s not found",orderEvent.customerId()),response.reason());
        });
    }

    protected void whenNotEnoughBalanceThenPaymentEventDeclined(OrderEvent.Created orderEvent){
        expectEvent(orderEvent, PaymentEvent.Declined.class, response -> {
            assertEquals(String.format("Customer with id %s doesn't have enough balance",orderEvent.customerId()),response.reason());
        });
    }
}
