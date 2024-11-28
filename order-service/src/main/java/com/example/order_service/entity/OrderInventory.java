package com.example.order_service.entity;

import com.example.order_service.enums.InventoryStatus;
import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderInventory extends BaseEntity{

    @Id
    private Long id;

    private UUID orderId;
    private UUID inventoryId;
    private String message;
    private InventoryStatus status;
    private Boolean success;
}
