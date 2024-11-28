package com.example.catalog_service.domain;

import java.math.BigDecimal;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@EqualsAndHashCode(callSuper = true)
@Table("products")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Product extends BaseEntity{
    @Id
    private Long id;

    private String code;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private Integer availableQuantity;
}
