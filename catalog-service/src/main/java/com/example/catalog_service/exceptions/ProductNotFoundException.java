package com.example.catalog_service.exceptions;

import com.example.catalog_service.util.Constants;

import static com.example.catalog_service.util.Constants.Exceptions.NOT_FOUND_ID;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String code) {
        super(String.format(Constants.Exceptions.NOT_FOUND_CODE, code));
    }

    public ProductNotFoundException(Long productId) {
        super(String.format(NOT_FOUND_ID, productId));
    }
}
