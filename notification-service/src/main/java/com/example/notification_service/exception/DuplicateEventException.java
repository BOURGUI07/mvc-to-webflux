package com.example.notification_service.exception;

import static com.example.notification_service.util.Constants.Exceptions.DUPLICATE_EVENT;

public class DuplicateEventException extends RuntimeException {
    public DuplicateEventException() {
        super(DUPLICATE_EVENT);
    }
}
