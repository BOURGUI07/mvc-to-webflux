package com.example.order_service.service;

import com.example.order_service.dto.OrderInventoryDTO;
import com.example.order_service.dto.OrderPaymentDTO;
import com.example.order_service.entity.OrderInventory;
import com.example.order_service.mapper.InventoryMapper;
import com.example.order_service.mapper.PaymentMapper;
import com.example.order_service.repo.InventoryRepo;
import com.example.order_service.repo.PaymentRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class InventoryService {
    private final InventoryRepo repo;
    private final OrderFulfillmentService service;

    public Mono<Void> handleSuccessfulInventory(OrderInventoryDTO dto){
        return save().apply(dto,true)
                .then(service.completeOrder(dto.orderId()));
    }

    public Mono<Void> handleFailedInventory(OrderInventoryDTO dto){
        return save().apply(dto,false)
                .then(service.cancelOrder(dto.orderId()));
    }

    public Mono<Void> handleRolledBackInventory(OrderInventoryDTO dto){
        return repo.findByOrderId(dto.orderId())
                .flatMap(inventory -> repo.save(inventory.setStatus(dto.status())))
                .then();
    }


    private BiFunction<OrderInventoryDTO, Boolean, Mono<OrderInventory>> save(){
        return (dto, success) -> repo.findByOrderId(dto.orderId())
                .defaultIfEmpty(InventoryMapper.toEntity().apply(dto))
                .flatMap(inventory -> repo.save(inventory.setSuccess(success)));
    }
}
