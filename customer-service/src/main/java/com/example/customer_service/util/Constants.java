package com.example.customer_service.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    public static final class Exceptions{
        public static final String DUPLICATE_EVENT = "Duplicate Event with orderId %s";
        public static final String NOT_FOUND_ID = "Customer with id %s not found";
        public static final String NOT_ENOUGH_BALANCE = "Customer with id %s doesn't have enough balance";
    }

    public static final class API_BASE_URL{
        public static final String CUSTOMER_BASE_URL = "/api/customers";
    }
}
