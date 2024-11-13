package com.example.catalog_service;

import com.example.catalog_service.dto.ProductCreationRequest;
import com.example.catalog_service.dto.ProductResponse;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.http.ProblemDetail;
import org.testcontainers.shaded.org.apache.commons.lang3.function.TriFunction;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractPostRequestTests extends AbstractIntegrationTest{

    protected Function<ProductCreationRequest,Duration> validCreateRequest(){
        return request -> {
            var productId = new AtomicLong();

            return client
                    .post()
                    .uri("/api/products")
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().is2xxSuccessful()
                    .returnResult(ProductResponse.class)
                    .getResponseBody()
                    .doOnNext(p-> productId.set(p.id()))
                    .as(StepVerifier::create)
                    .assertNext(response -> assertValidProductResponse().accept(response,request, productId.get()))
                    .verifyComplete();
        };
    }

    private TriConsumer<ProductResponse,ProductCreationRequest,Long> assertValidProductResponse(){
        return (response,request,id) -> {
          assertEquals(id, response.id());
          assertEquals(request.name(), response.name());
          assertEquals(request.description(), response.description());
          assertEquals(request.price(), response.price());
          assertEquals(request.code(), response.code());
        };
    }

    protected TriFunction<ProductCreationRequest,String,String,Duration> invalidCreationRequest(){
        return (request,detail,title) ->
             client
                    .post()
                    .uri("/api/products")
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().is4xxClientError()
                    .returnResult(ProblemDetail.class)
                    .getResponseBody()
                    .as(StepVerifier::create)
                    .assertNext(response -> assertInvalidCreationRequest().accept(response,detail,title))
                    .verifyComplete();

    }

    private TriConsumer<ProblemDetail,String,String> assertInvalidCreationRequest(){
        return (problemDetail,detail,title) ->{
            assertEquals(detail,problemDetail.getDetail());
            assertEquals(title,problemDetail.getTitle());
        };
    }


}
