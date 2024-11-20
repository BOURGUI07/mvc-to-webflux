package com.example.catalog_service.CRUD_tests;

import com.example.catalog_service.CRUD_abstract_tests.AbstractDeleteRequestTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(OutputCaptureExtension.class)
public class DeleteRequestTests extends AbstractDeleteRequestTests {


    @Test
    void validDeleteRequest(CapturedOutput output) {
        this.deleteById().accept("p100");
        assertTrue(output.getOut().contains("Product Deleted Successfully"));
    }

    @Test
    void invalidDeleteRequest() {
        this.deleteNotFound().apply("p1000");
    }
}
