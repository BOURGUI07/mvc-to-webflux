package com.example.catalog_service;

import com.example.catalog_service.dto.ProductCreationRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class PostRequestTests extends AbstractPostRequestTests{

    @Test
    void testValidProductCreationRequest(){
        var request = ProductCreationRequest.builder()
                .name("TestProduct")
                .description("TestDescription")
                .code("P888")
                .price(new BigDecimal("100.00"))
                .imageUrl("imageTest.jpg")
                .build();

        validCreateRequest().apply(request);
    }

    @Test
    void testInvalidPrice(){
        var request = ProductCreationRequest.builder()
                .name("TestProduct")
                .description("TestDescription")
                .code("P888")
                .price(new BigDecimal("0.00"))
                .imageUrl("imageTest.jpg")
                .build();

        var title = "Invalid Product Request";
        var detail = "Product Price is Invalid";

        invalidCreationRequest().apply(request,detail,title);
    }

    @Test
    void testNullName(){
        var request = ProductCreationRequest.builder()
                .description("TestDescription")
                .code("P888")
                .price(new BigDecimal("10.00"))
                .imageUrl("imageTest.jpg")
                .build();

        var title = "Invalid Product Request";
        var detail = "Product Name is Required";

        invalidCreationRequest().apply(request,detail,title);
    }

    @Test
    void testNullCode(){
        var request = ProductCreationRequest.builder()
                .name("TestProduct")
                .description("TestDescription")
                .price(new BigDecimal("10.00"))
                .imageUrl("imageTest.jpg")
                .build();

        var title = "Invalid Product Request";
        var detail = "Product Code is Required";

        invalidCreationRequest().apply(request,detail,title);
    }

    @Test
    void testNullDescription(){
        var request = ProductCreationRequest.builder()
                .name("TestProduct")
                .code("P888")
                .price(new BigDecimal("10.00"))
                .imageUrl("imageTest.jpg")
                .build();

        var title = "Invalid Product Request";
        var detail = "Product Description is Required";

        invalidCreationRequest().apply(request,detail,title);
    }

    @Test
    void testNullImageUrl(){
        var request = ProductCreationRequest.builder()
                .name("TestProduct")
                .description("TestDescription")
                .code("P888")
                .price(new BigDecimal("10.00"))
                .build();

        var title = "Invalid Product Request";
        var detail = "Product Image Url is Required";

        invalidCreationRequest().apply(request,detail,title);
    }


    @Test
    void testAlreadyExistsProduct(){
        var request = ProductCreationRequest.builder()
                .name("TestProduct")
                .description("TestDescription")
                .code("p100")
                .price(new BigDecimal("10.00"))
                .imageUrl("imageTest.jpg")
                .build();

        this.createExistingProduct().apply(request);
    }

}
