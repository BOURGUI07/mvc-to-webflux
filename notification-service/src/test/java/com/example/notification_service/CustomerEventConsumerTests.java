package com.example.notification_service;

import com.example.notification_service.events.CustomerEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(OutputCaptureExtension.class)
public class CustomerEventConsumerTests extends AbstractIntegrationTests{
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenCustomerEventCreatedThenNewCustomerSaved(){

        var event = CustomerEvent.Created.builder()
                .customerId(1L)
                .email("test@test.com")
                .username("test")
                .build();

     streamBridge.send("customer-events",event);

            Mono.delay(Duration.ofSeconds(1))
                    .then(repo.existsByCustomerId(1L))
                    .as(StepVerifier::create)
                    .expectNext(true)
                    .verifyComplete();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenCustomerAlreadyExistsThenCustomerDiscarded(CapturedOutput output) throws InterruptedException {

        var event = CustomerEvent.Created.builder()
                .customerId(1L)
                .email("test@test.com")
                .username("test")
                .build();

        streamBridge.send("customer-events",event);

        Mono.delay(Duration.ofSeconds(1))
                .then(repo.existsByCustomerId(1L))
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();

        streamBridge.send("customer-events",event);

        Thread.sleep(1000);

        assertTrue(output.getOut().contains("Discard customer event"));
    }


}
