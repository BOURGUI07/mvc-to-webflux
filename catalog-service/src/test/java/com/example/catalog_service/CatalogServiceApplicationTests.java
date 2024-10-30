package com.example.catalog_service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.catalog_service.dto.PagedResult;
import com.example.catalog_service.dto.ProductCreationResponse;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import reactor.test.StepVerifier;

class CatalogServiceApplicationTests extends AbstractIntegrationTest {

    @Test
    void textGetProducts() {
        client.get()
                .uri("/api/products")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(new ParameterizedTypeReference<PagedResult<ProductCreationResponse>>() {})
                .getResponseBody()
                .as(StepVerifier::create)
                .assertNext(response -> {
                    var firstBook = response.data().getFirst();
                    assertEquals("P111", firstBook.code());
                    assertTrue(response.isFirst());
                    assertFalse(response.isLast());
                    assertEquals(15, response.totalElements());
                    assertEquals(10, response.data().size());
                })
                .verifyComplete();
    }

    @Test
    void testProductStream() {
        client.get()
                .uri("/api/products/stream/40.5")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ProductCreationResponse.class)
                .getResponseBody()
                .collectList()
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertTrue(response.stream().allMatch(x -> x.price().compareTo(new BigDecimal("40.5")) < 0));
                })
                .verifyComplete();
    }
}
