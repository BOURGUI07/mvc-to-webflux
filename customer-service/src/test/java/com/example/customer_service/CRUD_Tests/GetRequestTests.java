package com.example.customer_service.CRUD_Tests;

import com.example.customer_service.AbstractIntegrationTests;
import com.example.customer_service.dto.CustomerDTO;
import com.example.customer_service.dto.PageResult;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetRequestTests extends AbstractIntegrationTests {

    @Test
    void testPageable(){
        client
                .get()
                .uri("/api/customers")
                .exchange()
                .expectStatus().isOk()
                .returnResult(new ParameterizedTypeReference<PageResult<CustomerDTO.Response>>(){})
                .getResponseBody()
                .as(StepVerifier::create)
                .assertNext(consumer())
                .verifyComplete();
    }


    private Consumer<PageResult<CustomerDTO.Response>> consumer(){
        return response -> {
            var firstData = response.data().getFirst();
          assertEquals(new BigDecimal(4700), firstData.balance());
          assertEquals("bob_brown", firstData.username());
          assertTrue(response.isFirst());
          assertTrue(response.isLast());
          assertEquals(1,response.pageNumber());
        };
    }
}
