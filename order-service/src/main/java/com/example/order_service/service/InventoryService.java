package com.example.order_service.service;

import com.example.order_service.dto.OrderInventoryDTO;
import com.example.order_service.dto.OrderPaymentDTO;
import com.example.order_service.mapper.InventoryMapper;
import com.example.order_service.mapper.PaymentMapper;
import com.example.order_service.repo.InventoryRepo;
import com.example.order_service.repo.PaymentRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class InventoryService {
    private final InventoryRepo repo;

    public Mono<Void> handleSuccessfulInventory(OrderInventoryDTO dto){
        return repo.findByOrderId(dto.orderId())
                .defaultIfEmpty(InventoryMapper.toEntity().apply(dto))
                .flatMap(inventory -> repo.save(inventory.setSuccess(true)))
                .then();
    }

    public Mono<Void> handleFailedInventory(OrderInventoryDTO dto){
        return repo.findByOrderId(dto.orderId())
                .defaultIfEmpty(InventoryMapper.toEntity().apply(dto))
                .flatMap(inventory -> repo.save(inventory.setSuccess(false)))
                .then();
    }

    public Mono<Void> handleRolledBackInventory(OrderInventoryDTO dto){
        return repo.findByOrderId(dto.orderId())
                .flatMap(inventory -> repo.save(inventory.setStatus(dto.status())))
                .then();
    }
}
