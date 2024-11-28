package com.example.order_service.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.order_service.AbstractIntegrationTests;
import com.example.order_service.events.ProductEvent;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;

public class ProductEventConsumerTests extends AbstractIntegrationTests {

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenProductEventCreatedThenNewProductSaved() throws InterruptedException {
        var event = ProductEvent.Created.builder()
                .code("p100")
                .price(new BigDecimal("100.00"))
                .productId(1L)
                .build();

        streamBridge.send("catalog-events", event);

        Thread.sleep(timeout);

        productRepo
                .findByCodeIgnoreCase("p100")
                .as(StepVerifier::create)
                .assertNext(product -> {
                    assertNotNull(product.getId());
                    assertEquals("p100", product.getCode());
                    assertEquals(new BigDecimal("100.00"), product.getPrice());
                    assertEquals(1L, product.getProductId());
                })
                .verifyComplete();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenProductEventUpdatedThenProductPriceUpdate() throws InterruptedException {
        var event = ProductEvent.Created.builder()
                .code("p100")
                .price(new BigDecimal("100.00"))
                .productId(1L)
                .build();

        streamBridge.send("catalog-events", event);

        Thread.sleep(timeout);

        productRepo
                .findByCodeIgnoreCase("p100")
                .as(StepVerifier::create)
                .assertNext(product -> {
                    assertNotNull(product.getId());
                    assertEquals("p100", product.getCode());
                    assertEquals(new BigDecimal("100.00"), product.getPrice());
                    assertEquals(1L, product.getProductId());
                })
                .verifyComplete();

        var event1 = ProductEvent.Updated.builder()
                .code("p100")
                .price(new BigDecimal("125.00"))
                .build();

        streamBridge.send("catalog-events", event1);

        Thread.sleep(timeout);

        productRepo
                .findByCodeIgnoreCase("p100")
                .as(StepVerifier::create)
                .assertNext(product -> {
                    assertNotNull(product.getId());
                    assertEquals("p100", product.getCode());
                    assertEquals(new BigDecimal("125.00"), product.getPrice());
                    assertEquals(1L, product.getProductId());
                })
                .verifyComplete();
    }

    @Test
    void whenProductEventDeletedThenProductDeleted() throws InterruptedException {
        var event = ProductEvent.Created.builder()
                .code("p100")
                .price(new BigDecimal("100.00"))
                .productId(1L)
                .build();

        streamBridge.send("catalog-events", event);

        Thread.sleep(timeout);

        productRepo
                .existsByCodeIgnoreCase("p100")
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();

        var event1 = ProductEvent.Deleted.builder().code("p100").build();

        streamBridge.send("catalog-events", event1);

        Thread.sleep(timeout);

        productRepo
                .existsByCodeIgnoreCase("p100")
                .as(StepVerifier::create)
                .expectNext(false)
                .verifyComplete();
    }
}
