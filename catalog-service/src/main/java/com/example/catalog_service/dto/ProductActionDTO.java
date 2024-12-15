package com.example.catalog_service.dto;

import com.example.catalog_service.domain.ProductAction;
import lombok.Builder;

@Builder
public record ProductActionDTO(
        ProductAction action,
        ProductResponse response
) {
}
