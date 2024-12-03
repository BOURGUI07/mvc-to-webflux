package com.example.ratingservice.CRUD_TESTS;

import com.example.ratingservice.AbstractIntegrationTests;
import com.example.ratingservice.dto.request.RatingUpdateRequest;
import com.example.ratingservice.dto.response.RatingResponse;
import com.example.ratingservice.entity.Rating;
import org.apache.logging.log4j.util.TriConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PutRequestTests extends AbstractIntegrationTests {

    private Supplier<Long> insertData(){
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

          ratingRepo.save(rating)
                  .doOnNext(entity -> atomicLong.set(entity.getRatingId()))
                  .then()
                  .as(StepVerifier::create)
                  .verifyComplete();

          return atomicLong.get();
        };
    }

    private TriConsumer<RatingUpdateRequest, Long,Consumer<RatingResponse>> validPutRequest(){
        return (request, ratingId,responseConsumer) -> {

            insertData().get();
          client.put()
                  .uri("/api/ratings/{ratingId}", ratingId)
                  .bodyValue(request)
                  .exchange()
                  .expectStatus()
                  .isOk()
                  .returnResult(RatingResponse.class)
                  .getResponseBody()
                  .as(StepVerifier::create)
                  .assertNext(response -> {
                              assertEquals(request.value(),response.value());
                              assertEquals(ratingId,response.ratingId());
                              responseConsumer.accept(response);
                          })
                  .verifyComplete();
        };
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testValidPutRequest(){
        var request = RatingUpdateRequest.builder()
                .value(4.5)
                .content("Delighted with the purchase")
                .title("good product")
                .build();
        validPutRequest().accept(request,insertData().get() ,response -> {
            assertEquals(request.content(),response.content());
            assertEquals(request.title(),response.title());
        });
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testValidPutRequestWithMissingTitleAndContent(){
        var request = RatingUpdateRequest.builder()
                .value(4.5)
                .build();
        validPutRequest().accept(request,insertData().get() ,response -> {});
    }

    private TriConsumer<RatingUpdateRequest, Long,String> invalidPutRequest(){
        return (request, ratingId, title) -> {
            insertData().get();
            client.put()
                    .uri("/api/ratings/{ratingId}", ratingId)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus()
                    .is4xxClientError()
                    .returnResult(ProblemDetail.class)
                    .getResponseBody()
                    .as(StepVerifier::create)
                    .assertNext(response -> {
                        assertEquals(title, response.getTitle());
                    })
                    .verifyComplete();

        };
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testInvalidPutRequestWithNotFoundRatingId(){
        var request = RatingUpdateRequest.builder()
                .value(4.4)
                .title("good product")
                .build();
        invalidPutRequest().accept(request,insertData().get()+99 ,"Rating Not Found");
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testInvalidPutRequestWithInvalidRatingValue(){
        var request = RatingUpdateRequest.builder()
                .value(5.4)
                .title("good product")
                .build();
        invalidPutRequest().accept(request,insertData().get() ,"Invalid Rating Request");
    }


}
