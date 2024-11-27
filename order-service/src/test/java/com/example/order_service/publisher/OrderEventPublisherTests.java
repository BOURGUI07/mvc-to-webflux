package com.example.order_service.publisher;


import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

public class OrderEventPublisherTests extends AbstractOrderEventPublisherTests{

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void test() throws InterruptedException {
        whenOrderPlacedThenOrderEventCreated(1L,1L,4);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testNullQuantity(){
        var detail = "Quantity is Required and Should be Positive";
        var title = "Invalid Order Request";

        invalidRequest(1L,1L,null,detail,title);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testInvalidQuantity(){
        var detail = "Quantity is Required and Should be Positive";
        var title = "Invalid Order Request";

        invalidRequest(1L,1L,-5,detail,title);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testNullProductId(){
        var detail = "Product Id is Required";
        var title = "Invalid Order Request";

        invalidRequest(null,1L,null,detail,title);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testNullCustomerId(){
        var detail = "Customer Id is Required";
        var title = "Invalid Order Request";

        invalidRequest(1L,null,45,detail,title);
    }
}
