package com.example.customer_service.exceptions;

import com.example.customer_service.util.Constants;

import java.util.UUID;

import static com.example.customer_service.util.Constants.Exceptions.DUPLICATE_EVENT;

public class DuplicateEventException extends RuntimeException {

    public DuplicateEventException(UUID orderId) {
        super(String.format(DUPLICATE_EVENT, orderId));
    }
}
