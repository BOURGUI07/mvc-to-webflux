package com.example.ratingservice.entity;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Builder
@Table
public class Rating extends BaseEntity {

    @Id
    private Long ratingId;
    private Long customerId;
    private Long productId;
    private Double value;
    private UUID orderId;
    private String title;
    private String content;
}
