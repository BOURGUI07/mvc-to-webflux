package com.example.shipping_service.domain;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Shipment extends BaseEntity{

    @Id
    private UUID shippingId;
    private UUID orderId;
    private Long customerId;
    private Long productId;
    private Instant deliveryDate;
    private Integer quantity;
    private ShippingStatus status;
}
