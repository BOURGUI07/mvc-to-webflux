package com.example.customer_service.domain;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class Customer extends BaseEntity{

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
