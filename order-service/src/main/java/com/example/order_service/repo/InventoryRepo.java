package com.example.order_service.repo;

import com.example.order_service.entity.OrderInventory;
import com.example.order_service.entity.PurchaseOrder;
import com.example.order_service.enums.InventoryStatus;
import com.example.order_service.enums.OrderStatus;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface InventoryRepo extends ReactiveCrudRepository<OrderInventory, Long> {
    Mono<OrderInventory> findByOrderId(UUID orderId);


}
