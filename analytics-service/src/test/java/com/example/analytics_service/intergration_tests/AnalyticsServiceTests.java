package com.example.analytics_service.intergration_tests;

import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnalyticsServiceTests extends AbstractIntegrationTests{

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void test() throws InterruptedException {
        viewProduct("p100",4);
        viewProduct("p122",2);
        viewProduct("p100",3);
        viewProduct("p122",6);
        viewProduct("p122",14);

        Thread.sleep(10000);

        testProductView();
    }
}
