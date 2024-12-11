package com.example.catalog_service.integration_tests.MESSAGING_abstract_tests;

import com.example.catalog_service.integration_tests.AbstractIntegrationTest;
import com.example.catalog_service.dto.ProductResponse;
import com.example.catalog_service.events.ProductEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(AbstractProductViewPublisherTest.testConfig.class)
@TestPropertySource(properties = {
        "spring.cloud.function.definition=productViewProducer;consumer",
        "spring.cloud.stream.bindings.consumer-in-0.destination=catalog-events"
})
public class AbstractProductViewPublisherTest extends AbstractIntegrationTest {
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


    protected void viewProduct(String code){
        client
                .get()
                .uri("/api/products/{code}",code)
                .exchange()
                .returnResult(ProductResponse.class)
                .getResponseBody()
                .thenMany(resFlux)
                .next()
                .cast(ProductEvent.View.class)
                .as(StepVerifier::create)
                .assertNext(event -> assertEquals(code, event.code()))
                .verifyComplete();
    }
}
