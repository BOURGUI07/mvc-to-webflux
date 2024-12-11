package com.example.catalog_service.integration_tests.MESSAGING_tests;

import com.example.catalog_service.integration_tests.MESSAGING_abstract_tests.AbstractProductEventPublisherTest;
import com.example.catalog_service.dto.ProductCreationRequest;
import com.example.catalog_service.dto.ProductUpdateRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class ProductEventPublisherTests extends AbstractProductEventPublisherTest {

    @Test
    void test1(){
        // test created product event

        var request = ProductCreationRequest.builder()
                .code("p555")
                .name("testProduct")
                .price(new BigDecimal("55.5"))
                .quantity(45)
                .description("testDescription")
                .imageUrl("imageUrl")
                .build();

        whenProductCreatedThenProductEventCreated(request);

        // test updated product event

        var updateRequest = ProductUpdateRequest.builder()
                .price(new BigDecimal("22.5"))
                .build();

        whenProductUpdatedThenProductEventUpdated("p555",updateRequest);


        // test deleted product event

        whenProductDeletedThenProductEventDeleted("p555");
    }
}
