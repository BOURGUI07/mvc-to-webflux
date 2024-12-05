package com.example.catalog_service.service;

import com.example.catalog_service.domain.InventoryStatus;
import com.example.catalog_service.domain.Product;
import com.example.catalog_service.domain.ProductInventory;
import com.example.catalog_service.dto.PurchaseDTO;
import com.example.catalog_service.exceptions.ApplicationsExceptions;
import com.example.catalog_service.mapper.Mapper;
import com.example.catalog_service.repo.ProductInventoryRepo;
import com.example.catalog_service.repo.ProductRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The Inventory-service is mainly concerned about processing
 * Deduct and Restore operations
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final ProductRepo repo;
    private final ProductInventoryRepo inventoryRepo;
    private final CacheService cacheService;


    /**
     * First, check if the orderId of request is already processed or not.
     * If so, Raise DuplicateEvent Exception.
     * Otherwise, find the product.
     * Check if he has enough inventory to deduct
     * If he doesn't, raise NotEnoughInventory Exception
     * Otherwise, convert the request into ProductInventory entity
     * then proceed into deduction process
     */
    public Mono<PurchaseDTO> processRequest(PurchaseDTO.Request request) {
        var orderId = request.orderId();
        return inventoryRepo.existsByOrderId(orderId)
                .filter(Predicate.not(b->b))
                .doOnDiscard(Boolean.class, b -> log.info("DISCARDED REQUEST WITH ORDER_ID: {}", orderId))
                .switchIfEmpty(ApplicationsExceptions.duplicateEvent(orderId))
                .then(repo.findById(request.productId()))
                .switchIfEmpty(ApplicationsExceptions.productNotFound(request.productId()))
                .filter(p -> p.getAvailableQuantity()>= request.quantity())
                .switchIfEmpty(ApplicationsExceptions.notEnoughInventory(request.productId()))
                .zipWhen(p -> Mono.fromSupplier(() -> Mapper.toProductInventoryEntity().apply(request)),executeProcess())
                .flatMap(Function.identity());

    }

    /**
     * First, lower the product quantity
     * Set the InventoryStatus to DEDUCTED
     * save the product, remove it from cache
     * save the productInventory entity.
     * convert it into DTO
     */
    private BiFunction<Product, ProductInventory,Mono<PurchaseDTO>> executeProcess(){
        return (product,inventory) -> repo.save(product).then(cacheService.doOnChanged(product))
                .then(inventoryRepo.save(inventory))
                .map(inv -> Mapper.toPurchaseDTO().apply(inv,product))
                .doFirst(() -> {
                   product.setAvailableQuantity(product.getAvailableQuantity()-inventory.getQuantity());
                   inventory.setStatus(InventoryStatus.DEDUCTED);
                });
    }


    /**
     * To restore a product quantity, it has to be already deducted
     * if so, then proceed to the restoration process
     */
    public Mono<PurchaseDTO> restore(UUID orderId){
        return inventoryRepo.findByOrderIdAndStatus(orderId,InventoryStatus.DEDUCTED)
                .zipWhen( inv -> repo.findById(inv.getProductId()), executeRestore())
                .flatMap(Function.identity());
    }


    /**
     * Restore the product quantity.
     * Update the ProductInventory Status to RESTORED
     * save the product, then remove it from cache
     * save the productInventory.
     * Convert it into DTO
     */
    private BiFunction<ProductInventory,Product,Mono<PurchaseDTO>> executeRestore(){
        return (inventory,product) -> repo.save(product).then(cacheService.doOnChanged(product))
                .then(inventoryRepo.save(inventory))
                .map(inv -> Mapper.toPurchaseDTO().apply(inv,product))
                .doFirst(() -> {
                    product.setAvailableQuantity(product.getAvailableQuantity()+inventory.getQuantity());
                    inventory.setStatus(InventoryStatus.RESTORED);
                });

    }

}
