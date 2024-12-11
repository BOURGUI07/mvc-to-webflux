package com.example.catalog_service.dto;

import lombok.Builder;

@Builder
public record ProductCreationBulkErrorDetail(
        int rowNumber,
        String errorMessage
) {
}
