package com.example.catalog_service.integration_tests.CRUD_tests;

import com.example.catalog_service.integration_tests.CRUD_abstract_tests.AbstractProductBulkPostRequestTests;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

public class ProductBulkPostRequestTests extends AbstractProductBulkPostRequestTests {

    @Test
    @DisplayName("Test Successful Creation of All Products")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testCreateProducts_whenAllLinesProcessedSuccessfully_AllProductsCreated(){
        String filePath = getClass().getClassLoader().getResource("all_success.csv").getPath();
        test().accept(filePath, response -> {
            assertEquals(3,response.getCreatedProducts().size(),"List size should have returned 3");
            assertEquals(3,response.getProcessed(),"should have processed 3 products");
            assertEquals(3,response.getSuccess(), "should have succeeded  3 times");
            assertEquals(0,response.getFailure(),"should have failed 0 times");
            assertTrue(response.getErrorDetails().isEmpty(),"should have empty error detail list");
        });
    }


    @Test
    @DisplayName("Test Unsuccessful Creation of All Products")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Disabled("To Fix Later")
    void testCreateProducts_whenSomeProductsFail_ThenSomeCreated(){
        String filePath = getClass().getClassLoader().getResource("all_failure.csv").getPath();
        test().accept(filePath, response -> {
            assertEquals(2,response.getCreatedProducts().size(),"List size should have returned 2");
            assertEquals(3,response.getProcessed(),"should have processed 3 products");
            assertEquals(2,response.getSuccess(), "should have succeeded  2 times");
            assertEquals(1,response.getFailure(),"should have failed 1 times");
            assertFalse(response.getErrorDetails().isEmpty(),"should have non-empty empty error detail list");
        });
    }
}
