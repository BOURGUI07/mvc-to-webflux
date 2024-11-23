package com.example.notification_service.exception;

public class EmailFailureException extends RuntimeException {
    private final static String MESSAGE = "Failed to send email";

    public EmailFailureException() {
        super(MESSAGE);
    }
}
