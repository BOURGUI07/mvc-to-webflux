package com.example.order_service.entity;

import com.example.order_service.enums.PaymentStatus;
import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderPayment {
    @Id
    private Long id;

    private UUID orderId;
    private UUID paymentId;
    private String message;
    private PaymentStatus status;
    private Boolean success;
}
