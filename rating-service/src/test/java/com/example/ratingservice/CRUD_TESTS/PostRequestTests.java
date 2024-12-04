package com.example.ratingservice.CRUD_TESTS;

import static org.junit.jupiter.api.Assertions.*;

import com.example.ratingservice.AbstractIntegrationTests;
import com.example.ratingservice.dto.request.RatingCreationRequest;
import com.example.ratingservice.dto.response.RatingResponse;
import com.example.ratingservice.entity.OrderHistory;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.apache.logging.log4j.util.TriConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;

public class PostRequestTests extends AbstractIntegrationTests {

    private UUID insertData() {
        var orderHistory = OrderHistory.builder()
                .orderId(UUID.randomUUID())
                .customerId(1L)
                .productId(1L)
                .build();

        orderRepo.save(orderHistory).then().as(StepVerifier::create).verifyComplete();

        return orderHistory.getOrderId();
    }

    // POST REQUEST
    private BiConsumer<RatingCreationRequest, Consumer<RatingResponse>> validPostRequest() {
        return (request, responseConsumer) -> {
            insertData();
            client.post()
                    .uri("/api/ratings")
                    .bodyValue(request)
                    .exchange()
                    .expectStatus()
                    .isCreated()
                    .returnResult(RatingResponse.class)
                    .getResponseBody()
                    .as(StepVerifier::create)
                    .assertNext(response -> {
                        assertEquals(request.value(), response.value());
                        assertNotNull(response.ratingId());
                        assertEquals(request.customerId(), response.customerId());
                        assertEquals(request.productId(), response.productId());
                        responseConsumer.accept(response);
                    })
                    .verifyComplete();
        };
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testValidRequest() {
        var request = RatingCreationRequest.builder()
                .customerId(1L)
                .productId(1L)
                .title("bad product")
                .content("I'm disappointed with the product, definitely not as advertised!")
                .value(2.5)
                .orderId(insertData())
                .build();

        validPostRequest().accept(request, response -> {
            assertEquals(request.content(), response.content());
            assertEquals(request.title(), response.title());
        });
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testValidRequestWithMissingContentAndTitle() {
        var request = RatingCreationRequest.builder()
                .customerId(1L)
                .productId(1L)
                .value(2.5)
                .orderId(insertData())
                .build();

        validPostRequest().accept(request, response -> {
            assertNull(response.content());
            assertNull(response.title());
        });
    }

    private TriConsumer<RatingCreationRequest, String, String> invalidPostRequest() {
        return (request, detail, title) -> {
            insertData();
            client.post()
                    .uri("/api/ratings")
                    .bodyValue(request)
                    .exchange()
                    .expectStatus()
                    .is4xxClientError()
                    .returnResult(ProblemDetail.class)
                    .getResponseBody()
                    .as(StepVerifier::create)
                    .assertNext(response -> {
                        assertEquals(title, response.getTitle());
                        assertEquals(detail, response.getDetail());
                    })
                    .verifyComplete();
        };
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testPostRequestWithMissingCustomerId() {
        var request = RatingCreationRequest.builder()
                .productId(1L)
                .orderId(insertData())
                .value(2.5)
                .build();

        var detail = "Customer id is required";
        var title = "Invalid Rating Request";
        invalidPostRequest().accept(request, detail, title);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testPostRequestWithMissingProductId() {
        var request = RatingCreationRequest.builder()
                .customerId(1L)
                .orderId(insertData())
                .value(2.5)
                .build();

        var detail = "Product id is required";
        var title = "Invalid Rating Request";
        invalidPostRequest().accept(request, detail, title);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testPostRequestWithMissingOrderId() {
        var request = RatingCreationRequest.builder()
                .customerId(1L)
                .productId(1L)
                .value(2.5)
                .build();

        var detail = "Order id is required";
        var title = "Invalid Rating Request";
        invalidPostRequest().accept(request, detail, title);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testPostRequestWithMissingRatingValue() {
        var request = RatingCreationRequest.builder()
                .customerId(1L)
                .productId(1L)
                .orderId(insertData())
                .build();

        var detail = "Value is required and should be between 1 and 5";
        var title = "Invalid Rating Request";
        invalidPostRequest().accept(request, detail, title);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testPostRequestWithInvalidRatingValue() {
        var request = RatingCreationRequest.builder()
                .customerId(1L)
                .productId(1L)
                .orderId(insertData())
                .value(0.5)
                .build();

        var detail = "Value is required and should be between 1 and 5";
        var title = "Invalid Rating Request";
        invalidPostRequest().accept(request, detail, title);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testPostRequestWithNotFoundOrderId() {
        var request = RatingCreationRequest.builder()
                .customerId(1L)
                .productId(1L)
                .orderId(UUID.randomUUID())
                .value(1.5)
                .build();

        var detail = "Order id %s not found".formatted(request.orderId());
        var title = "Invalid Rating Request";
        invalidPostRequest().accept(request, detail, title);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testPostRequestWithInvalidProductId() {
        var request = RatingCreationRequest.builder()
                .customerId(1L)
                .productId(2L)
                .orderId(insertData())
                .value(1.5)
                .build();

        var detail = "Either productId or customerId doesn't match existing productId or customerId.";
        var title = "Invalid Rating Request";
        invalidPostRequest().accept(request, detail, title);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testPostRequestWithInvalidCustomerId() {
        var request = RatingCreationRequest.builder()
                .customerId(2L)
                .productId(1L)
                .orderId(insertData())
                .value(1.5)
                .build();

        var detail = "Either productId or customerId doesn't match existing productId or customerId.";
        var title = "Invalid Rating Request";
        invalidPostRequest().accept(request, detail, title);
    }
}
