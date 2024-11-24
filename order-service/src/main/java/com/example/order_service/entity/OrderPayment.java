package com.example.order_service.entity;

import com.example.order_service.enums.InventoryStatus;
import com.example.order_service.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class OrderPayment {
    @Id
    private Long id;
    private UUID orderId;
    private UUID paymentId;
    private String message;
    private PaymentStatus status;
    private Boolean success;
}
