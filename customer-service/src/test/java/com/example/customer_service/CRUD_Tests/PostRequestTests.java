package com.example.customer_service.CRUD_Tests;

import com.example.customer_service.dto.CustomerDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class PostRequestTests extends AbstractPostRequestTests{


    @Test
    void test(){
        var request = CustomerDTO.Request.builder()
                .balance(new BigDecimal(5000))
                .phone("1234567889")
                .state("testData")
                .city("testCity")
                .country("testCountry")
                .email("testEmail@gmail.com")
                .street("testStreet")
                .username("testUsername")
                .build();

        validPostRequest().apply(request);
    }


    @Test
    void missingBalance(){
        var request = CustomerDTO.Request.builder()
                .phone("1234567889")
                .state("testData")
                .city("testCity")
                .country("testCountry")
                .email("testEmail@gmail.com")
                .street("testStreet")
                .username("testUsername")
                .build();

        var detail = "Balance is Required and Must be Positive";
        var title= "Invalid Customer Request";
        invalidPostRequest().apply(request,detail,title);
    }

    @Test
    void invalidBalance(){
        var request = CustomerDTO.Request.builder()
                .phone("1234567889")
                .state("testData")
                .city("testCity")
                .country("testCountry")
                .email("testEmail@gmail.com")
                .street("testStreet")
                .username("testUsername")
                .balance(new BigDecimal(0))
                .build();

        var detail = "Balance is Required and Must be Positive";
        var title= "Invalid Customer Request";
        invalidPostRequest().apply(request,detail,title);
    }

    @Test
    void invalidEmail(){
        var request = CustomerDTO.Request.builder()
                .balance(new BigDecimal(5000))
                .phone("1234567889")
                .state("testData")
                .city("testCity")
                .country("testCountry")
                .email("testEmail@")
                .street("testStreet")
                .username("testUsername")
                .build();

        var detail = "Email is Required and Must be Valid";
        var title= "Invalid Customer Request";
        invalidPostRequest().apply(request,detail,title);
    }

    @Test
    void alreadyExistingUsername(){
        var request = CustomerDTO.Request.builder()
                .balance(new BigDecimal(5000))
                .phone("1234567889")
                .state("testData")
                .city("testCity")
                .country("testCountry")
                .email("testEmail@gmail.com")
                .street("testStreet")
                .username("john_doe")
                .build();

        createAlreadyExistingUsername().apply(request);
    }

    @Test
    void alreadyExistingEmail(){
        var request = CustomerDTO.Request.builder()
                .balance(new BigDecimal(5000))
                .phone("1234567889")
                .state("testData")
                .city("testCity")
                .country("testCountry")
                .email("jane.smith@example.com")
                .street("testStreet")
                .username("testUsername")
                .build();

        createAlreadyExistingEmail().apply(request);
    }
}
