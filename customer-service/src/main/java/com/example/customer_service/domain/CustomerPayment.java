package com.example.customer_service.domain;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class CustomerPayment extends BaseEntity{

    @Id
    private UUID paymentId;
    private UUID orderId;
    private Long customerId;
    private BigDecimal amount;
    private PaymentStatus status;
}
