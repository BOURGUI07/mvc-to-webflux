package com.example.customer_service.dto;

import lombok.Builder;

import java.math.BigDecimal;

public sealed interface CustomerDTO {

    String username();
    String city();
    String state();
    String country();
    String phone();
    String email();
    String street();

    @Builder
    record Request(
            String username,
            String phone,
            String email,
            String city,
            String street,
            String state,
            String country,
            BigDecimal balance
    ) implements CustomerDTO {}


    @Builder
    record Response(
            Long customerId,
            String username,
            String phone,
            String email,
            String city,
            String street,
            String state,
            String country,
            BigDecimal balance
    ) implements CustomerDTO {}
}
