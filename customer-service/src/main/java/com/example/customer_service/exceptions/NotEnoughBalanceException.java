package com.example.customer_service.exceptions;

public class NotEnoughBalanceException extends RuntimeException {
    private static final String MESSAGE = "Customer with id %s doesn't have enough balance";

    public NotEnoughBalanceException(Long customerId) {
        super(String.format(MESSAGE, customerId));
    }
}
