package com.example.customer_service.MESSAGING_Tests;

import com.example.customer_service.AbstractIntegrationTests;
import com.example.customer_service.dto.CustomerDTO;
import com.example.customer_service.events.CustomerEvent;
import com.example.customer_service.events.PaymentEvent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Import(CustomerEventProducerTest.testConfig.class)
@TestPropertySource(properties = {
        "spring.cloud.function.definition=customerEventProducer;consumer",
        "spring.cloud.stream.bindings.consumer-in-0.destination=customer-events"
})
public class CustomerEventProducerTest extends AbstractIntegrationTests {
    private final static Sinks.Many<CustomerEvent> sink = Sinks.many().unicast().onBackpressureBuffer();
    private final static Flux<CustomerEvent> resFlux = sink.asFlux().cache(0);

    @TestConfiguration
    static class testConfig{
        @Bean
        public Consumer<Flux<CustomerEvent>> consumer(){
            return flux -> flux
                    .doOnNext(sink::tryEmitNext)
                    .subscribe();
        }
    }


    @Test
    void whenNewCustomerCreatedThenCustomerEventCreated(){
        var request = CustomerDTO.Request.builder()
                        .username("username")
                .balance(new BigDecimal(500))
                .street("street")
                .city("city")
                .state("state")
                .country("country")
                .phone("phone")
                .email("youness@gmail.com")
                .build();

        client
                .post()
                .uri("/api/customers")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .returnResult(CustomerDTO.Response.class)
                .getResponseBody()
                .thenMany(resFlux)
                .next()
                .timeout(Duration.ofSeconds(10))
                .cast(CustomerEvent.Created.class)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals("username",response.username());
                    assertEquals("youness@gmail.com",response.email());
                    assertNotNull(response.customerId());
                })
                .verifyComplete();


    }
}
