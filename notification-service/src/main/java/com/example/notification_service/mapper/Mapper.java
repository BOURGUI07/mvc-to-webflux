package com.example.notification_service.mapper;

import com.example.notification_service.entity.Customer;
import com.example.notification_service.events.CustomerEvent;
import com.example.notification_service.events.OrderEvent;

import java.util.function.Function;

public class Mapper {


    public static Function<CustomerEvent.Created, Customer> toCustomer(){
        return event -> Customer.builder()
                .customerId(event.customerId())
                .email(event.email())
                .username(event.username())
                .build();
    }


}
