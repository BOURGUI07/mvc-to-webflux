package com.example.customer_service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class CustomerPayment {

    @Id
    private UUID paymentId;
    private UUID orderId;
    private Long customerId;
    private BigDecimal amount;
    private PaymentStatus status;
}
