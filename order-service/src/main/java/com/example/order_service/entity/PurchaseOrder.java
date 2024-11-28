package com.example.order_service.entity;

import com.example.order_service.enums.OrderStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseOrder extends BaseEntity{

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
