package com.example.catalog_service.domain;


import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Table
public class ProductInventory extends BaseEntity{
    @Id
    private UUID inventoryId;
    private UUID orderId;
    private Long productId;
    private Integer quantity;
    private InventoryStatus status;
}
