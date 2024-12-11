package com.example.catalog_service.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class ProductCreationBulkResponse {
    int processed;
    int success;
    List<ProductResponse> createdProducts;
    int failure;
    List<ProductCreationBulkErrorDetail> errorDetails;


    public void incrementSuccess() {
        success++;
    }

    public void incrementFailure() {
        failure++;
    }

    public void incrementProcessed() {
        processed++;
    }

    public void addCreatedProduct(ProductResponse productResponse) {
        createdProducts.add(productResponse);
    }

    public void addErrorDetail(int rowNumber, String errorMessage) {
        errorDetails.add(new ProductCreationBulkErrorDetail(rowNumber, errorMessage));
    }
}
