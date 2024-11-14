package com.example.catalog_service;

import com.example.catalog_service.dto.ProductUpdateRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class PutRequestTests extends AbstractPutRequestTests{

    @Test
    void validUpdateRequestTest(){
        var request = ProductUpdateRequest.builder()
                .name("TestName")
                .description("TestDescription")
                .price(new BigDecimal("24.00"))
                .imageUrl("testImageUrl.jpg")
                .build();

        this.validUpdateRequest().apply(request,"p100");
    }

    @Test
    void invalidPrice(){
        var request = ProductUpdateRequest.builder()
                .name("TestName")
                .description("TestDescription")
                .price(new BigDecimal("0.00"))
                .imageUrl("testImageUrl.jpg")
                .build();

        var detail = "Product Price is Invalid";
        var title = "Invalid Product Request";
        this.invalidUpdateRequest(request,"p100",detail,title);
    }

    @Test
    void nullImageUrl(){
        var request = ProductUpdateRequest.builder()
                .name("TestName")
                .description("TestDescription")
                .price(new BigDecimal("10.00"))
                .build();

        var detail = "Product Image Url is Required";
        var title = "Invalid Product Request";
        this.invalidUpdateRequest(request,"p100",detail,title);
    }

    @Test
    void nullDescription(){
        var request = ProductUpdateRequest.builder()
                .name("TestName")
                .price(new BigDecimal("10.00"))
                .imageUrl("testImageUrl.jpg")
                .build();

        var detail = "Product Description is Required";
        var title = "Invalid Product Request";
        this.invalidUpdateRequest(request,"p100",detail,title);
    }

    @Test
    void nullName(){
        var request = ProductUpdateRequest.builder()
                .description("TestDescription")
                .price(new BigDecimal("20.00"))
                .imageUrl("testImageUrl.jpg")
                .build();

        var detail = "Product Name is Required";
        var title = "Invalid Product Request";
        this.invalidUpdateRequest(request,"p100",detail,title);
    }

    @Test
    void updateNotFoundProduct(){
        var request = ProductUpdateRequest.builder()
                .name("TestName")
                .description("TestDescription")
                .price(new BigDecimal("20.00"))
                .imageUrl("testImageUrl.jpg")
                .build();

        this.updateNotExistingProduct().apply(request,"p1000");
    }
}
