package com.example.customer_service.mapper;

import com.example.customer_service.domain.Customer;
import com.example.customer_service.dto.CustomerDTO;

import java.util.function.Function;

public class CustomerMapper {

    public static Function<CustomerDTO.Request, Customer> toEntity(){
        return request ->  Customer.builder()
                .balance(request.balance())
                .phone(request.phone())
                .city(request.city())
                .state(request.state())
                .country(request.country())
                .email(request.email())
                .username(request.username())
                .street(request.street())
                .build();
    }

    public static Function<Customer,CustomerDTO> toDTO(){
        return entity ->  CustomerDTO.Response.builder()
                .balance(entity.getBalance())
                .phone(entity.getPhone())
                .city(entity.getCity())
                .state(entity.getState())
                .country(entity.getCountry())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .street(entity.getStreet())
                .customerId(entity.getId())
                .build();
    }
}
