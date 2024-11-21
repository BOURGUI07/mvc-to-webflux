package com.example.customer_service.exceptions;

import java.util.UUID;

public class DuplicateEventException extends RuntimeException {
    private static final String MESSAGE = "Duplicate Event with orderId %s";

    public DuplicateEventException(UUID orderId) {
        super(String.format(MESSAGE, orderId));
    }
}
