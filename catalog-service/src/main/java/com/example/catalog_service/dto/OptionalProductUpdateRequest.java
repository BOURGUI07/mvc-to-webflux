package com.example.catalog_service.dto;

import lombok.Builder;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.Optional;

@Builder
public record OptionalProductUpdateRequest(
        Optional<String> name,
        Optional<String> description,
        Optional<String> imageUrl,
        Optional<BigDecimal> price
) {
}
