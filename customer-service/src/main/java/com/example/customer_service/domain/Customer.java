package com.example.customer_service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class Customer {

    @Id
    private Long id;
    private String username;
    private BigDecimal balance;
    private String email;
    private String phone;
    private String city;
    private String state;
    private String country;
    private String street;

}
