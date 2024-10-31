package com.example.catalog_service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.catalog_service.dto.PagedResult;
import com.example.catalog_service.dto.ProductCreationResponse;
import java.math.BigDecimal;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import reactor.test.StepVerifier;

public class CatalogServiceGetRequestTests extends AbstractIntegrationTest {

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

    @Test
    void testValidGetProduct() {
        client.get()
                .uri("/api/products/p111")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ProductCreationResponse.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals("P111", response.code());
                    assertEquals(new BigDecimal("32.0"), response.price());
                    assertEquals("A Game of Thrones", response.name());
                })
                .verifyComplete();
    }

    @Test
    void testNotFoundGetProduct() {
        client.get()
                .uri("/api/products/p999")
                .exchange()
                .expectStatus()
                .isNotFound()
                .returnResult(ProblemDetail.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals("Product Not Found", response.getTitle());
                    assertEquals("Product with code p999 not found", response.getDetail());
                    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
                    assertEquals("RESOURCE_NOT_FOUND", response.getProperties().get("errorCategory"));
                    assertEquals(URI.create("/api/products/p999"), response.getInstance());
                })
                .verifyComplete();
    }

    @Test
    void testGeneralError() {
        client.get()
                .uri("/api/products/p999/99")
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .returnResult(ProblemDetail.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals("General Error", response.getTitle());
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
                    assertEquals("GENERAL_ERROR", response.getProperties().get("errorCategory"));
                    assertEquals(URI.create("/api/products/p999/99"), response.getInstance());
                })
                .verifyComplete();
    }
}
