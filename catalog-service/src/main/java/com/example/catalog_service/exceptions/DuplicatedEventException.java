package com.example.catalog_service.exceptions;

import java.util.UUID;

public class DuplicatedEventException extends RuntimeException {

    private static final String MESSAGE = "Duplicated Event With OrderId: %s";

    public DuplicatedEventException(UUID orderId) {
        super(String.format(MESSAGE, orderId));
    }
}
