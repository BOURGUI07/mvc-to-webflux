package com.example.catalog_service;

import com.example.catalog_service.dto.ProductCreationRequest;
import com.example.catalog_service.dto.ProductResponse;
import com.example.catalog_service.dto.ProductUpdateRequest;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.http.ProblemDetail;
import org.testcontainers.shaded.org.apache.commons.lang3.function.TriFunction;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractPutRequestTests extends AbstractIntegrationTest {

    protected BiFunction<ProductUpdateRequest, String,Duration> validUpdateRequest(){
        return (request,code) -> {
            var productId = new AtomicLong();

            return client
                    .put()
                    .uri("/api/products/{code}",code)
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

    private TriConsumer<ProductResponse,ProductUpdateRequest,Long> assertValidProductResponse(){
        return (response,request,id) -> {
            assertEquals(id, response.id());
            assertEquals(request.name(), response.name());
            assertEquals(request.description(), response.description());
            assertEquals(request.price(), response.price());
        };
    }

    protected Duration invalidUpdateRequest(ProductUpdateRequest request, String code, String detail,String title){
        return client
                        .put()
                        .uri("/api/products/{code}", code)
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

    protected BiFunction<ProductUpdateRequest, String,Duration> updateNotExistingProduct(){
        return (request,code) ->
                client
                        .put()
                        .uri("/api/products/{code}",code)
                        .bodyValue(request)
                        .exchange()
                        .returnResult(ProblemDetail.class)
                        .getResponseBody()
                        .as(StepVerifier::create)
                        .assertNext(response -> assertNotExistingProduct().accept(response,code))
                        .verifyComplete();
    }

    private BiConsumer<ProblemDetail,String> assertNotExistingProduct(){
        return (problemDetail,code)->{
            assertEquals("Product with code " + code + " not found",problemDetail.getDetail());
            assertEquals("Product Not Found",problemDetail.getTitle());
        };
    }
}
