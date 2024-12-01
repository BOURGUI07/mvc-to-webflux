package com.example.ratingservice.entity;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Builder
@Table
public class OrderHistory extends BaseEntity{

    @Id
    private Long id;
    private UUID orderId;
    private Long productId;
    private Long customerId;
}
