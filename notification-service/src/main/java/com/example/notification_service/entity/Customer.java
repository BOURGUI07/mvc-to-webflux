package com.example.notification_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Accessors(chain = true)
@Builder
public class Customer {

    @Id
    private Long id;
    private Long customerId;
    private String username;
    private String email;
}
