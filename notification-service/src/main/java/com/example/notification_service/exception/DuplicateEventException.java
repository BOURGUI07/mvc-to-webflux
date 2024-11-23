package com.example.notification_service.exception;

public class DuplicateEventException extends RuntimeException {
    private static final String MESSAGE = "Duplicate Event Exception";
    public DuplicateEventException() {
        super(MESSAGE);
    }
}
