package com.example.customer_service.exceptions;

public class CustomerNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Customer with id %s not found";

    public CustomerNotFoundException(Long customerId) {
        super(String.format(MESSAGE, customerId));
    }
}
