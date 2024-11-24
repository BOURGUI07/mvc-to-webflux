package com.example.catalog_service.MESSAGING_tests;

import com.example.catalog_service.MESSAGING_abstract_tests.AbstractProductViewPublisherTest;
import org.junit.jupiter.api.Test;

public class ProductViewPublisherTests extends AbstractProductViewPublisherTest {

    @Test
    void test(){
        viewProduct("p100");
        viewProduct("p114");
        viewProduct("p102");
    }
}
