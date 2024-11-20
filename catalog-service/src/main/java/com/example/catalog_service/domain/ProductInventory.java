package com.example.catalog_service.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ProductInventory {
    @Id
    private UUID inventoryId;
    private UUID orderId;
    private Long productId;
    private Integer quantity;
    private InventoryStatus status;
}
