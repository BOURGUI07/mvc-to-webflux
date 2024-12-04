package com.example.ratingservice.CRUD_TESTS;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.ratingservice.AbstractIntegrationTests;
import com.example.ratingservice.entity.Rating;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;

public class DeleteRequestTests extends AbstractIntegrationTests {
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
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testValidDeleteRequest() {
        var ratingId = insertData().get();

        client.delete()
                .uri("/api/ratings/" + ratingId)
                .exchange()
                .expectStatus()
                .isNoContent()
                .returnResult(Void.class)
                .getResponseBody()
                .then()
                .as(StepVerifier::create)
                .verifyComplete();

        ratingRepo.findById(ratingId).as(StepVerifier::create).verifyComplete();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testInvalidDeleteRequest() {
        var ratingId = insertData().get() + 99;

        client.delete()
                .uri("/api/ratings/" + ratingId)
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .returnResult(ProblemDetail.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .assertNext(resp -> assertEquals("Rating Not Found", resp.getTitle()))
                .verifyComplete();
    }
}
