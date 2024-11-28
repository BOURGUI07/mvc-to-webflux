package com.example.order_service.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    @Id
    private Long id;

    private Long productId;
    private String code;
    private BigDecimal price;
}
