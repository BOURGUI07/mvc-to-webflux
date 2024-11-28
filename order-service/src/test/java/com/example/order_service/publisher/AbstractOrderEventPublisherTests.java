package com.example.order_service.publisher;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.order_service.AbstractIntegrationTests;
import com.example.order_service.dto.OrderDTO;
import com.example.order_service.entity.Product;
import com.example.order_service.enums.OrderStatus;
import com.example.order_service.events.OrderEvent;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.shaded.org.apache.commons.lang3.function.TriFunction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

@Import(AbstractOrderEventPublisherTests.testConfig.class)
@TestPropertySource(
        properties = {
            "spring.cloud.function.definition=orderEventProducer;consumer",
            "spring.cloud.stream.bindings.consumer-in-0.destination=order-events"
        })
@Slf4j
public class AbstractOrderEventPublisherTests extends AbstractIntegrationTests {
    private static final Sinks.Many<OrderEvent> sink = Sinks.many().unicast().onBackpressureBuffer();
    private static final Flux<OrderEvent> resFlux = sink.asFlux().cache(0);

    @TestConfiguration
    static class testConfig {
        @Bean
        public Consumer<Flux<OrderEvent>> consumer() {
            return flux -> flux.doOnNext(sink::tryEmitNext).subscribe();
        }
    }

    private final Product product = Product.builder()
            .price(new BigDecimal("100.00"))
            .productId(1L)
            .code("p100")
            .build();

    public void insertData() {
        productRepo.save(product).then().as(StepVerifier::create).verifyComplete();
    }

    static class testUtil {
        public static OrderDTO.Request createRequest(Long productId, Long customerId, Integer quantity) {
            return OrderDTO.Request.builder()
                    .productId(productId)
                    .customerId(customerId)
                    .quantity(quantity)
                    .build();
        }
    }

    private TriFunction<Long, Long, Integer, Duration> validRequest() {
        return (productId, customerId, quantity) -> {
            insertData();
            var request = testUtil.createRequest(productId, customerId, quantity);
            return client.post()
                    .uri("/api/orders")
                    .bodyValue(request)
                    .exchange()
                    .returnResult(OrderDTO.Response.class)
                    .getResponseBody()
                    .as(StepVerifier::create)
                    .assertNext(response -> {
                        assertEquals(request.customerId(), response.customerId());
                        assertEquals(request.productId(), response.productId());
                        assertEquals(request.quantity(), response.quantity());
                        assertEquals(OrderStatus.CREATED, response.status());
                    })
                    .verifyComplete();
        };
    }

    protected Duration invalidRequest(Long productId, Long customerId, Integer quantity, String detail, String title) {
        insertData();
        var request = testUtil.createRequest(productId, customerId, quantity);
        return client.post()
                .uri("/api/orders")
                .bodyValue(request)
                .exchange()
                .returnResult(ProblemDetail.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(detail, response.getDetail());
                    assertEquals(title, response.getTitle());
                })
                .verifyComplete();
    }

    protected void whenOrderPlacedThenOrderEventCreated(Long productId, Long customerId, Integer quantity)
            throws InterruptedException {
        validRequest().apply(productId, customerId, quantity);

        Thread.sleep(5000);

        // verify all orders
        client.get()
                .uri("/api/orders")
                .exchange()
                .returnResult(OrderDTO.Response.class)
                .getResponseBody()
                .collectList()
                .doOnNext(list -> log.info("ORDERS LIST: {}", list))
                .as(StepVerifier::create)
                .assertNext(list -> {
                    var firstOrder = list.getFirst();
                    assertEquals(productId, firstOrder.productId());
                    assertEquals(customerId, firstOrder.customerId());
                    assertEquals(quantity, firstOrder.quantity());
                })
                .verifyComplete();

        resFlux.next()
                .cast(OrderEvent.Created.class)
                .timeout(Duration.ofSeconds(10))
                .as(StepVerifier::create)
                .assertNext(event -> {
                    assertEquals(productId, event.productId());
                    assertEquals(customerId, event.customerId());
                    assertEquals(quantity, event.quantity());
                })
                .verifyComplete();
    }
}
