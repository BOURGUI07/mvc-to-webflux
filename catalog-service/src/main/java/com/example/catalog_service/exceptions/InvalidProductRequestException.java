package com.example.catalog_service.exceptions;

public class InvalidProductRequestException extends RuntimeException {
    public InvalidProductRequestException(String message) {
        super(message);
    }
}
