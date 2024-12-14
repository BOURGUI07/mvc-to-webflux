package com.example.catalog_service.unit_tests;

import com.example.catalog_service.domain.Product;
import com.example.catalog_service.dto.CatalogServiceProperties;
import com.example.catalog_service.repo.ProductRepo;
import com.example.catalog_service.service.cache.ProductCacheService;
import com.example.catalog_service.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {
    @Mock
    private ProductCacheService cacheService;

    @Mock
    private ProductRepo repo;

    @Mock
    private CatalogServiceProperties properties;

    @InjectMocks
    private ProductService service;


    @Test
    @DisplayName("Test product stream")
    void testProductStream(){
        //Arrange
        var flux = Flux.just(
                Product.builder()
                        .code("p112")
                .name("testName")
                .description("testDescription")
                        .price(new BigDecimal("47.00"))
                        .availableQuantity(45)
                        .id(4L)
                        .imageUrl("testUrl")
                        .build()

        );

        Mockito.when(cacheService.findAll()).thenReturn(flux);

        //Act and Assert
        service.getProductStream(new BigDecimal("49.28"))
                .collectList()
                .as(StepVerifier::create)
                .expectNextMatches(list -> list.size() == 1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Test Paged Result")
    void testPageableResponse(){
        //Arrange
        var flux = Flux.just(
                Product.builder()
                        .code("p112")
                        .name("testName")
                        .description("testDescription")
                        .price(new BigDecimal("47.00"))
                        .availableQuantity(45)
                        .id(4L)
                        .imageUrl("testUrl")
                        .build()

        );

        Mockito.when(properties.defaultPageSize()).thenReturn(10);
        Mockito.when(repo.count()).thenReturn(Mono.just(1L));
        Mockito.when(repo.findBy(Mockito.any(Pageable.class))).thenReturn(flux);


        //Act and Assert

        service.getProducts(1)
                .as(StepVerifier::create)
                .expectNextMatches(result -> result.data().size() == 1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Test find by code for an existing product")
    void findByCode_WhenProductExists_ThenShouldReturnProduct(){
        var existingProduct = Product.builder()
                .code("p112")
                .name("testName")
                .description("testDescription")
                .price(new BigDecimal("47.00"))
                .availableQuantity(45)
                .id(4L)
                .imageUrl("testUrl")
                .build();

        Mockito.when(cacheService.findByCode(Mockito.anyString())).thenReturn(Mono.just(existingProduct));

        service.findByCode("p112")
                .as(StepVerifier::create)
                .expectNextMatches(product -> product.code().equals("p112"))
                .verifyComplete();
    }



}
