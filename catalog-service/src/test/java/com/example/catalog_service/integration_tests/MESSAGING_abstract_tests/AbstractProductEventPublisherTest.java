package com.example.catalog_service.integration_tests.MESSAGING_abstract_tests;

import com.example.catalog_service.integration_tests.AbstractIntegrationTest;
import com.example.catalog_service.dto.ProductCreationRequest;
import com.example.catalog_service.dto.ProductResponse;
import com.example.catalog_service.dto.ProductUpdateRequest;
import com.example.catalog_service.events.ProductEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(AbstractProductEventPublisherTest.testConfig.class)
@TestPropertySource(properties = {
        "spring.cloud.function.definition=producer;consumer",
        "spring.cloud.stream.bindings.consumer-in-0.destination=catalog-events"
})
public class AbstractProductEventPublisherTest extends AbstractIntegrationTest {

    private final static Sinks.Many<ProductEvent> sink = Sinks.many().unicast().onBackpressureBuffer();
    private final static Flux<ProductEvent> resFlux = sink.asFlux().cache(0);

    @TestConfiguration
    static class testConfig{
        @Bean
        public Consumer<Flux<ProductEvent>> consumer(){
            return flux -> flux
                    .doOnNext(sink::tryEmitNext)
                    .subscribe();
        }
    }


    protected void whenProductCreatedThenProductEventCreated(ProductCreationRequest request){
        var productId = new AtomicLong();

        client
                .post()
                .uri("/api/products")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(ProductResponse.class)
                .getResponseBody()
                .doOnNext(x -> productId.set(x.id()))
                .then()
                .as(StepVerifier::create)
                .verifyComplete();

        resFlux
                .next()
                .timeout(Duration.ofMillis(timeout))
                .cast(ProductEvent.Created.class)
                .as(StepVerifier::create)
                .assertNext(event -> {
                    assertEquals(request.code(), event.code());
                    assertEquals(request.price(), event.price());
                    assertEquals(productId.get(), event.productId());
                })
                .verifyComplete();
    }

    protected void whenProductUpdatedThenProductEventUpdated(String code, ProductUpdateRequest request){

        client
                .put()
                .uri("/api/products/{code}", code)
                .bodyValue(request)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(ProductResponse.class)
                .getResponseBody()
                .then()
                .as(StepVerifier::create)
                .verifyComplete();

        resFlux
                .next()
                .timeout(Duration.ofMillis(timeout))
                .cast(ProductEvent.Updated.class)
                .as(StepVerifier::create)
                .assertNext(event -> {
                    assertEquals(request.price(), event.price());
                    assertEquals(code, event.code());
                })
                .verifyComplete();
    }

    protected void whenProductDeletedThenProductEventDeleted(String code){

        client
                .delete()
                .uri("/api/products/{code}", code)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(Void.class)
                .getResponseBody()
                .then()
                .as(StepVerifier::create)
                .verifyComplete();

        resFlux
                .next()
                .timeout(Duration.ofMillis(timeout))
                .cast(ProductEvent.Deleted.class)
                .as(StepVerifier::create)
                .assertNext(event -> {
                    assertEquals(code, event.code());
                })
                .verifyComplete();
    }



}
