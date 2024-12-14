package com.example.catalog_service.integration_tests.CRUD_tests;

import com.example.catalog_service.integration_tests.CRUD_abstract_tests.AbstractDeleteRequestTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(OutputCaptureExtension.class)
public class DeleteRequestTests extends AbstractDeleteRequestTests {


    @Test
    @DisplayName("Test Valid Delete Request")
    void testDeleteProduct_WhenProductIsFound_ThenProductDeleted(CapturedOutput output) {
        this.deleteById().accept("p100");
        assertTrue(output.getOut().contains("Product Deleted Successfully"));
    }

    @Test
    @DisplayName("Test Invalid Delete Request")
    void testDeleteProduct_WhenProductIsNotFound_ThenExceptionThrown() {
        this.deleteNotFound().apply("p1000");
    }
}
