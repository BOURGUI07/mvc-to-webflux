package com.example.shipping_service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Shipment {

    @Id
    private UUID shippingId;
    private UUID orderId;
    private Long customerId;
    private Long productId;
    private Instant deliveryDate;
    private Integer quantity;
    private ShippingStatus status;
}
