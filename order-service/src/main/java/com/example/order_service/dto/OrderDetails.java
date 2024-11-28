package com.example.order_service.dto;

import lombok.Builder;

@Builder
public record OrderDetails(
        OrderDTO.Response order, OrderInventoryDTO inventory, OrderPaymentDTO payment, OrderShippingDTO shipping) {}
