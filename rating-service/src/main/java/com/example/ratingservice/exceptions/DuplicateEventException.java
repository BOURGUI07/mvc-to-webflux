package com.example.ratingservice.exceptions;

import static com.example.ratingservice.util.Constants.Exceptions.DUPLICATE_EVENT;

public class DuplicateEventException extends RuntimeException {
    public DuplicateEventException() {
        super(DUPLICATE_EVENT);
    }
}
