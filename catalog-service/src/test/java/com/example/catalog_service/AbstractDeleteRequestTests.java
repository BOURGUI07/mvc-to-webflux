package com.example.catalog_service;

import org.springframework.http.ProblemDetail;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractDeleteRequestTests extends AbstractIntegrationTest{

    protected Consumer<String> deleteById(){
        return code -> {
          client
                  .delete()
                  .uri("/api/products/{code}", code)
                  .exchange()
                  .expectStatus().isOk()
                  .returnResult(Void.class)
                  .getResponseBody()
                  .then()
                  .as(StepVerifier::create)
                  .verifyComplete();
        };
    }

    protected Function<String, Duration> deleteNotFound(){
        return (code) ->
                client
                        .delete()
                        .uri("/api/products/{code}",code)
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
