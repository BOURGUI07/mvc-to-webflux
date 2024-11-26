package com.example.customer_service.exceptions;

import com.example.customer_service.util.Constants;

import static com.example.customer_service.util.Constants.Exceptions.NOT_FOUND_ID;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long customerId) {
        super(String.format(NOT_FOUND_ID, customerId));
    }
}
