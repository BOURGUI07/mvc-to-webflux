package com.example.order_service.entity;

import com.example.order_service.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PurchaseOrder {

    @Id
    private UUID orderId;
    private Long productId;
    private Long customerId;
    private Integer quantity;
    private BigDecimal price;
    private OrderStatus status;
    private BigDecimal amount;


    @Version
    private Integer version;
}
