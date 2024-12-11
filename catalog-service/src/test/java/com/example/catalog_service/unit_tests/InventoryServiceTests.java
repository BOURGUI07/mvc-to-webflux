package com.example.catalog_service.unit_tests;

import com.example.catalog_service.domain.InventoryStatus;
import com.example.catalog_service.domain.Product;
import com.example.catalog_service.domain.ProductInventory;
import com.example.catalog_service.dto.PurchaseDTO;
import com.example.catalog_service.exceptions.DuplicatedEventException;
import com.example.catalog_service.exceptions.NotEnoughInventoryException;
import com.example.catalog_service.exceptions.ProductNotFoundException;
import com.example.catalog_service.repo.ProductInventoryRepo;
import com.example.catalog_service.repo.ProductRepo;
import com.example.catalog_service.service.CacheService;
import com.example.catalog_service.service.InventoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTests {
    @Mock
    private  ProductRepo repo;

    @Mock
    private  ProductInventoryRepo inventoryRepo;

    @Mock
    private  CacheService cacheService;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    @DisplayName("Test Process Request With Existing OrderId")
    void testProcessRequest_WhenOrderIsFound_ThenDuplicateExceptionThrown(){
        //Arrange
        var request = PurchaseDTO.Request.builder()
                .orderId(UUID.randomUUID())
                .productId(1L)
                .quantity(44)
                .build();

        Mockito.when(inventoryRepo.existsByOrderId(request.orderId())).thenReturn(Mono.just(true));
        Mockito.when(repo.findById(request.productId())).thenReturn(Mono.empty());

        //Act and Assert
        inventoryService.processRequest(request)
                .as(StepVerifier::create)
                .verifyError(DuplicatedEventException.class);

    }

    @Test
    @DisplayName("Test Process Request With Not Found Product")
    void testProcessRequest_WhenProductNotFound_ThenNotFoundExceptionThrown(){
        //Arrange
        var request = PurchaseDTO.Request.builder()
                .orderId(UUID.randomUUID())
                .productId(1L)
                .quantity(44)
                .build();

        Mockito.when(inventoryRepo.existsByOrderId(request.orderId())).thenReturn(Mono.just(false));
        Mockito.when(repo.findById(request.productId())).thenReturn(Mono.empty());

        //Act and Assert
        inventoryService.processRequest(request)
                .as(StepVerifier::create)
                .verifyError(ProductNotFoundException.class);

    }

    @Test
    @DisplayName("Test Process Request With Not Enough Quantity")
    void testProcessRequest_WhenNotEnoughBalance_ThenNotEnoughInventoryThrown(){
        //Arrange
        var request = PurchaseDTO.Request.builder()
                .orderId(UUID.randomUUID())
                .productId(1L)
                .quantity(44)
                .build();

        var product = Product.builder()
                        .id(1L)
                                .availableQuantity(40)
                                        .build();

        Mockito.when(inventoryRepo.existsByOrderId(request.orderId())).thenReturn(Mono.just(false));
        Mockito.when(repo.findById(request.productId())).thenReturn(Mono.fromSupplier(() -> product));

        //Act and Assert
        inventoryService.processRequest(request)
                .as(StepVerifier::create)
                .verifyError(NotEnoughInventoryException.class);

    }



}
