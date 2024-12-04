package com.example.ratingservice.CRUD_TESTS;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.ratingservice.AbstractIntegrationTests;
import com.example.ratingservice.dto.response.PaginatedRatingResponse;
import com.example.ratingservice.entity.Rating;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public class GetRequestsTests extends AbstractIntegrationTests {

    private Supplier<Long> insertData() {
        return () -> {
            var atomicLong = new AtomicLong();
            var rating = Rating.builder()
                    .customerId(1L)
                    .productId(1L)
                    .orderId(UUID.randomUUID())
                    .value(2.6)
                    .title("bad product")
                    .content("Disappointed")
                    .build();

            ratingRepo
                    .save(rating)
                    .doOnNext(entity -> atomicLong.set(entity.getRatingId()))
                    .then()
                    .as(StepVerifier::create)
                    .verifyComplete();

            return atomicLong.get();
        };
    }

    @Test
    void testGetRatingsByCustomerId() {
        insertData().get();
        client.get()
                .uri("/api/ratings/customers/{customerId}", 1L)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(PaginatedRatingResponse.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .assertNext(ratingResponse -> {
                    assertEquals(2.6, ratingResponse.maxRating());
                    assertEquals(2.6, ratingResponse.minRating());
                    assertEquals(2.6, ratingResponse.averageRating());
                    assertEquals(1L, ratingResponse.count());
                })
                .verifyComplete();
    }

    @Test
    void testGetRatingsByProductId() {
        insertData().get();
        client.get()
                .uri("/api/ratings/products/{productId}", 1L)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(PaginatedRatingResponse.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .assertNext(ratingResponse -> {
                    assertEquals(2.6, ratingResponse.maxRating());
                    assertEquals(2.6, ratingResponse.minRating());
                    assertEquals(2.6, ratingResponse.averageRating());
                    assertEquals(1L, ratingResponse.count());
                })
                .verifyComplete();
    }

    @Test
    void whenProductIdNotFoundThenEmpty() {
        insertData().get();
        client.get()
                .uri("/api/ratings/products/{productId}", 99L)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(PaginatedRatingResponse.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .assertNext(ratingResponse -> {
                    assertEquals(-1, ratingResponse.maxRating());
                    assertEquals(-1, ratingResponse.minRating());
                    assertEquals(-1, ratingResponse.averageRating());
                    assertEquals(0, ratingResponse.count());
                })
                .verifyComplete();
        ;
    }

    @Test
    void whenCustomerNotFoundThenEmpty() {
        insertData().get();
        client.get()
                .uri("/api/ratings/customers/{customerId}", 99L)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(PaginatedRatingResponse.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .assertNext(ratingResponse -> {
                    assertEquals(-1, ratingResponse.maxRating());
                    assertEquals(-1, ratingResponse.minRating());
                    assertEquals(-1, ratingResponse.averageRating());
                    assertEquals(0, ratingResponse.count());
                })
                .verifyComplete();
        ;
    }
}
