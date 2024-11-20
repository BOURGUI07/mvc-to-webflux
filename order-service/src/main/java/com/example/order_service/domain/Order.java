package com.example.order_service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;

@Table(name ="orders")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class Order {
    @Id
    private Long id;

    private String orderNumber;

    private String username;

    private String customerName;

    private String customerEmail;

    private String customerPhone;

    private String deliveryAddressLine1;

    private String deliveryAddressLine2;

    private String deliveryAddressCity;

    private String deliveryAddressState;

    private String deliveryAddressZipCode;

    private String deliveryAddressCountry;

    private OrderStatus status;
    private String comments;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

}
