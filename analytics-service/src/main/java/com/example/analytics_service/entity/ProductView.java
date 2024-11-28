package com.example.analytics_service.entity;


import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ProductView extends BaseEntity{

    @Id
    private Long id;
    private String productCode;
    private Long viewCount;
}
