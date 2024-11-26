package com.example.shipping_service.exception;

import com.example.shipping_service.util.Constants;

public class DuplicateEventException extends RuntimeException {

    public DuplicateEventException() {
        super(Constants.Exceptions.DUPLICATE_EVENT);
    }
}
