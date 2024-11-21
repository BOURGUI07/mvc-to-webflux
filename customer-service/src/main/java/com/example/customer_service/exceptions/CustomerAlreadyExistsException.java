package com.example.customer_service.exceptions;

public class CustomerAlreadyExistsException extends RuntimeException {
    private static final String MESSAGE ="Customer with email %s and username %s already exists";

    public CustomerAlreadyExistsException(String email,String username) {
        super(String.format(MESSAGE,email,username));
    }
}
