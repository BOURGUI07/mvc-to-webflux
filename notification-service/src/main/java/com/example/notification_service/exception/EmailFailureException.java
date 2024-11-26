package com.example.notification_service.exception;

import static com.example.notification_service.util.Constants.Exceptions.EMAIL_FAILURE;

public class EmailFailureException extends RuntimeException {

    public EmailFailureException() {
        super(EMAIL_FAILURE);
    }
}
