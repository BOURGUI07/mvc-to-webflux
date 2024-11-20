package com.example.catalog_service.CRUD_tests;

import com.example.catalog_service.CRUD_abstract_tests.AbstractPutRequestTests;
import com.example.catalog_service.dto.ProductUpdateRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class PutRequestTests extends AbstractPutRequestTests {

    @Test
    void validUpdateRequestTest(){
        var request = ProductUpdateRequest.builder()
                .name("TestName")
                .description("TestDescription")
                .price(new BigDecimal("24.00"))
                .imageUrl("testImageUrl.jpg")
                .quantity(44)
                .build();

        this.validUpdateRequest().apply(request,"p100");
    }


    @Test
    void updateNotFoundProduct(){
        var request = ProductUpdateRequest.builder()
                .name("TestName")
                .description("TestDescription")
                .price(new BigDecimal("20.00"))
                .imageUrl("testImageUrl.jpg")
                .quantity(44)
                .build();

        this.updateNotExistingProduct().apply(request,"p1000");
    }

    @Test
    void updateOnlyName(){
        var request = ProductUpdateRequest.builder()
                .name("TestName")
                .build();

        this.validUpdateRequest().apply(request,"p100");
    }

    @Test
    void updateOnlyDescription(){
        var request = ProductUpdateRequest.builder()
                .description("TestDescription")
                .build();

        this.validUpdateRequest().apply(request,"p100");
    }

    @Test
    void updateOnlyImageUrl(){
        var request = ProductUpdateRequest.builder()
                .imageUrl("testImageUrl.jpg")
                .build();

        this.validUpdateRequest().apply(request,"p100");
    }

    @Test
    void updateOnlyPrice(){
        var request = ProductUpdateRequest.builder()
                .price(new BigDecimal("20.00"))
                .build();

        this.validUpdateRequest().apply(request,"p100");
    }

    @Test
    void updateOnlyQuantity(){
        var request = ProductUpdateRequest.builder()
                .quantity(77)
                .build();

        this.validUpdateRequest().apply(request,"p100");
    }

    @Test
    void updateWithInvalidPrice(){
        var request = ProductUpdateRequest.builder()
                .price(new BigDecimal("00.00"))
                .build();

        var detail = "Product Price is Invalid";
        var title = "Invalid Product Request";

        this.invalidUpdateRequest(request,"p100",detail,title);
    }

    @Test
    void updateWithInvalidQuantity(){
        var request = ProductUpdateRequest.builder()
                .quantity(0)
                .build();

        var detail = "Product Quantity is Invalid";
        var title = "Invalid Product Request";

        this.invalidUpdateRequest(request,"p100",detail,title);
    }
}
