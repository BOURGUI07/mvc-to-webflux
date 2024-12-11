package com.example.catalog_service.integration_tests.CRUD_abstract_tests;

import com.example.catalog_service.dto.ProductCreationBulkResponse;
import com.example.catalog_service.integration_tests.AbstractIntegrationTest;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AbstractProductBulkPostRequestTests extends AbstractIntegrationTest {

    protected BiConsumer<String, Consumer<ProductCreationBulkResponse>> test(){
        return (filePath, consumer) -> client
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products/bulk")
                        .queryParam("filePath", filePath)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductCreationBulkResponse.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .consumeNextWith(consumer)
                .verifyComplete();
    }
}
