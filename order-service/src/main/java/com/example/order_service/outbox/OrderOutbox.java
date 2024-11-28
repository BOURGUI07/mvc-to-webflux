package com.example.order_service.outbox;

import com.example.order_service.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Table
public class OrderOutbox {

    @Id
    private Long id;

    private byte[] message;
    private OrderStatus status;
}
