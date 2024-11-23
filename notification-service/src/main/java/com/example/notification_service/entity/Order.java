package com.example.notification_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Accessors(chain = true)
@Builder
@Table(name="orders")
public class Order {
    @Id
    private Long id;
    private UUID orderId;
    private OrderStatus status;
}
