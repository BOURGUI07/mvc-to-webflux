package com.example.ratingservice.exceptions;

import static com.example.ratingservice.util.Constants.Exceptions.NOT_FOUND;

public class RatingNotFoundException extends RuntimeException {
    public RatingNotFoundException(Long ratingId) {
        super(String.format(NOT_FOUND, ratingId));
    }
}
