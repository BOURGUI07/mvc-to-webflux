package com.example.customer_service.CRUD_Tests;


import com.example.customer_service.AbstractIntegrationTests;
import com.example.customer_service.dto.CustomerDTO;
import org.apache.commons.lang3.function.TriFunction;
import org.springframework.http.ProblemDetail;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class AbstractPostRequestTests extends AbstractIntegrationTests {

    protected Function<CustomerDTO.Request, Duration> validPostRequest(){
        return request -> client
                .post()
                .uri("/api/customers")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .returnResult(CustomerDTO.Response.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .assertNext(response -> consumer().accept(request, response))
                .verifyComplete();
    }

    private BiConsumer<CustomerDTO.Request, CustomerDTO.Response> consumer(){
        return (request, response) -> {
          assertNotNull(response.customerId());
          assertEquals(request.balance(), response.balance());
          assertEquals(request.username(), response.username());
        };
    }

    protected TriFunction<CustomerDTO.Request, String,String,Duration> invalidPostRequest(){
        return (request, detail, title) -> client
                .post()
                .uri("/api/customers")
                .bodyValue(request)
                .exchange()
                .expectStatus().is4xxClientError()
                .returnResult(ProblemDetail.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(detail, response.getDetail());
                    assertEquals(title, response.getTitle());
                })
                .verifyComplete();
    }


    protected Function<CustomerDTO.Request, Duration> createAlreadyExistingUsername(){
        return request -> client
                .post()
                .uri("/api/customers")
                .bodyValue(request)
                .exchange()
                .expectStatus().is4xxClientError()
                .returnResult(ProblemDetail.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals("Customer Already Exists", response.getTitle());
                    assertEquals(String.format("Customer with username %s already exists",request.username()),response.getDetail());
                })
                .verifyComplete();
    }


    protected Function<CustomerDTO.Request, Duration> createAlreadyExistingEmail(){
        return request -> client
                .post()
                .uri("/api/customers")
                .bodyValue(request)
                .exchange()
                .expectStatus().is4xxClientError()
                .returnResult(ProblemDetail.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals("Customer Already Exists", response.getTitle());
                    assertEquals(String.format("Customer with email %s already exists",request.email()),response.getDetail());
                })
                .verifyComplete();
    }

}
