package com.example.analytics_service.unit_tests;

import com.example.analytics_service.entity.ProductView;
import com.example.analytics_service.events.ProductEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class ProductViewServiceTests extends AbstractUnitTest{

    @Test
    @DisplayName("Test findAll")
    void testFindAll_whenInvoked_ThenReturnFluxOfProducts() {
        //Arrange
        var flux = Flux.just(
                ProductView.builder().viewCount(25L).productCode("P111").build(),
                ProductView.builder().viewCount(15L).productCode("P151").build(),
                ProductView.builder().viewCount(5L).productCode("P113").build()
        );

        Mockito.when(repo.findTop5ByOrderByViewCountDesc()).thenReturn(flux);

        //Act and Assert

        service.products()
                .collectList()
                .as(StepVerifier::create)
                .expectNextMatches(list -> list.size() == 3)
                .verifyComplete();
    }

    @Test
    @DisplayName("Test consume() for new product")
    void testConsume_WhenNewProductIsConsumed_ThenItsViewCountInitializedAtOne(){
        //Arrange
        var event = ProductEvent.View.builder().code("P111").build();
        var expectedProductView = Mono.fromSupplier(() ->
                ProductView.builder().viewCount(1L).productCode("P111").build());

        Mockito.when(repo.findByProductCode(Mockito.anyString())).thenReturn(Mono.empty());
        Mockito.when(repo.save(Mockito.any(ProductView.class))).thenReturn(expectedProductView);

        //Act
        service.consume().apply(event)
                .as(StepVerifier::create)
                .verifyComplete();

        //Assert
        Mockito.verify(repo, Mockito.times(1)).save(Mockito.argThat(
                x-> x.getViewCount() == 1 && x.getProductCode().equals("P111")));

    }


    @Test
    @DisplayName("Test consume() for existing product")
    void testConsume_WhenExistingProductIsConsumed_ThenItsViewCountIncremented(){
        //Arrange
        var event = ProductEvent.View.builder().code("P111").build();

        var existingProductView = Mono.fromSupplier(() ->
                ProductView.builder().viewCount(17L).productCode("P111").build());
        var expectedProductView = Mono.fromSupplier(() ->
                ProductView.builder().viewCount(18L).productCode("P111").build());

        Mockito.when(repo.findByProductCode(Mockito.anyString())).thenReturn(existingProductView);
        Mockito.when(repo.save(Mockito.any(ProductView.class))).thenReturn(expectedProductView);

        //Act
        service.consume().apply(event)
                .as(StepVerifier::create)
                .verifyComplete();

        //Assert
        Mockito.verify(repo, Mockito.times(1)).save(Mockito.argThat(
                x-> x.getViewCount() == 18L && x.getProductCode().equals("P111")));

    }
}
