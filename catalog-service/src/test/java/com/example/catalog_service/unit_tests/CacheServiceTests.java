package com.example.catalog_service.unit_tests;

import com.example.catalog_service.domain.Product;
import com.example.catalog_service.exceptions.ProductNotFoundException;
import com.example.catalog_service.repo.ProductRepo;
import com.example.catalog_service.service.cache.ProductCacheService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveHashOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static com.example.catalog_service.util.Constants.RedisKeys.PRODUCT_KEY;

@ExtendWith(MockitoExtension.class)
public class CacheServiceTests {
    @Mock
    private  ProductRepo repo;

    @Mock
    private  ReactiveHashOperations<String, String, Product> operations;

    @InjectMocks
    private ProductCacheService service;

    @Test
    @DisplayName("Test findAll()")
    void testFindAll(){
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

        Mockito.when(operations.values(PRODUCT_KEY)).thenReturn(flux);

        //Act and Assert
        service.findAll()
                .collectList()
                .as(StepVerifier::create)
                .expectNextMatches(list -> list.size() == 1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Test find by existing product code in cache")
    void testFindByCode_WhenProductFoundInBothRepoAndCache_ThenReturnProduct(){

        //Arrange
        var product = Product.builder()
                .code("p112")
                .name("testName")
                .description("testDescription")
                .price(new BigDecimal("47.00"))
                .availableQuantity(45)
                .id(4L)
                .imageUrl("testUrl")
                .build();

        Mockito.when(repo.findByCodeIgnoreCase(Mockito.anyString())).thenReturn(Mono.just(product));
        Mockito.when(operations.get(PRODUCT_KEY,"p112")).thenReturn(Mono.just(product));

        //Act and Assert

        service.findByCode("p112")
                .as(StepVerifier::create)
                .expectNextMatches(entity -> entity.getAvailableQuantity()==45)
                .verifyComplete();
    }


    @Test
    @DisplayName("Test find by existing product code in repo")
    void testFindByCode_WhenProductFoundInBothRepo_ThenReturnProduct(){

        //Arrange
        var product = Product.builder()
                .code("p112")
                .name("testName")
                .description("testDescription")
                .price(new BigDecimal("47.00"))
                .availableQuantity(45)
                .id(4L)
                .imageUrl("testUrl")
                .build();


        Mockito.when(operations.get(PRODUCT_KEY,"p112")).thenReturn(Mono.empty());
        Mockito.when(repo.findByCodeIgnoreCase(Mockito.anyString())).thenReturn(Mono.just(product));
        Mockito.when(operations.put(Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(Mono.empty());

        //Act and Assert

        service.findByCode("p112")
                .as(StepVerifier::create)
                .expectNextMatches(entity -> entity.getAvailableQuantity()==45)
                .verifyComplete();
    }


    @Test
    @DisplayName("Test find by code for absent product")
    void testFindByCode_WhenProductNotFoundNeitherInCacheNorInRepo_ThenNotFoundExceptionRaised(){
        //Arrange
        Mockito.when(operations.get(PRODUCT_KEY,"p112")).thenReturn(Mono.empty());
        Mockito.when(repo.findByCodeIgnoreCase(Mockito.anyString())).thenReturn(Mono.empty());

        //Act and Assert

        service.findByCode("p112")
                .as(StepVerifier::create)
                .verifyError(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("Test onChanged()")
    void testOnChanged(){
        //Arrange
        var product = Product.builder()
                .code("p112")
                .name("testName")
                .description("testDescription")
                .price(new BigDecimal("47.00"))
                .availableQuantity(45)
                .id(4L)
                .imageUrl("testUrl")
                .build();
        Mockito.when(operations.remove(PRODUCT_KEY,"p112")).thenReturn(Mono.just(1L));

        //Act and Assert

        service.doOnChanged(product)
                .as(StepVerifier::create)
                .expectNext(1L)
                .verifyComplete();
    }


}
