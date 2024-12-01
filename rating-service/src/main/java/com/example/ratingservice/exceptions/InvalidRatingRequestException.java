package com.example.ratingservice.exceptions;

public class InvalidRatingRequestException extends RuntimeException {

    public InvalidRatingRequestException(String message) {
        super(message);
    }
}
