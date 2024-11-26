package com.example.customer_service.exceptions;

import com.example.customer_service.util.Constants;

import static com.example.customer_service.util.Constants.Exceptions.NOT_ENOUGH_BALANCE;

public class NotEnoughBalanceException extends RuntimeException {

    public NotEnoughBalanceException(Long customerId) {
        super(String.format(NOT_ENOUGH_BALANCE, customerId));
    }
}
