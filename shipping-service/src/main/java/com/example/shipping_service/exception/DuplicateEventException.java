package com.example.shipping_service.exception;

public class DuplicateEventException extends RuntimeException {
    private static final String MESSAGE = "Duplicate Event";

    public DuplicateEventException() {
        super(MESSAGE);
    }
}
