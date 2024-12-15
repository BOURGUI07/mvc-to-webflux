package com.example.catalog_service.mapper;

import com.example.catalog_service.domain.Product;
import com.example.catalog_service.domain.ProductAction;
import com.example.catalog_service.domain.ProductInventory;
import com.example.catalog_service.dto.*;
import com.example.catalog_service.events.InventoryEvent;
import com.example.catalog_service.events.OrderEvent;
import com.example.catalog_service.events.ProductEvent;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Mapper {
    public static Function<ProductCreationRequest,Product> toEntity( ) {
        return request -> Product.builder()
                .code(request.code())
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .imageUrl(request.imageUrl())
                .availableQuantity(request.quantity())
                .build();
    }

    public static Function<Product, ProductResponse> toDto() {
        return entity -> ProductResponse.builder()
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .id(entity.getId())
                .availableQuantity(entity.getAvailableQuantity())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static PagedResult<ProductResponse> toPagedResult(
            List<ProductResponse> data, long count, int page, int size) {
        int totalPages = (int) Math.ceil((double) count / size);
        var isFirst = page == 0;
        var isLast = page == totalPages - 1;
        return PagedResult.<ProductResponse>builder()
                .data(data)
                .totalPages(totalPages)
                .totalElements(count)
                .hasNext(!isLast)
                .hasPrevious(!isFirst)
                .isFirst(isFirst)
                .isLast(isLast)
                .pageNumber(page + 1)
                .build();
    }

    public static Function<OrderEvent.Created, PurchaseDTO.Request> toPurchaseRequest(){
        return event -> PurchaseDTO.Request.builder()
                .orderId(event.orderId())
                .productId(event.productId())
                .quantity(event.quantity())
                .build();
    }

    public static Function<PurchaseDTO.Request, ProductInventory> toProductInventoryEntity(){
        return request -> ProductInventory.builder()
                .productId(request.productId())
                .quantity(request.quantity())
                .orderId(request.orderId())
                .build();
    }

    public static BiFunction<ProductInventory,Product,PurchaseDTO> toPurchaseDTO(){
        return (entity,product) -> PurchaseDTO.Response.builder()
                .inventoryId(entity.getInventoryId())
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .status(entity.getStatus())
                .orderId(entity.getOrderId())
                .price(product.getPrice())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static Function<PurchaseDTO.Response, InventoryEvent> toDeducted(){
        return dto -> InventoryEvent.Deducted.builder()
                .deductedQty(dto.quantity())
                .inventoryId(dto.inventoryId())
                .productId(dto.productId())
                .orderId(dto.orderId())
                .price(dto.price())
                .build();
    }


    public static Function<PurchaseDTO.Response, InventoryEvent> toRestored(){
        return dto -> InventoryEvent.Restored.builder()
                .restoredQty(dto.quantity())
                .inventoryId(dto.inventoryId())
                .productId(dto.productId())
                .orderId(dto.orderId())
                .build();
    }

    public static BiFunction<Throwable,OrderEvent.Created,Mono<InventoryEvent>> toDeclined(){
        return (ex,event) -> Mono.fromSupplier(() ->InventoryEvent.Declined.builder()
                .declinedQty(event.quantity())
                .orderId(event.orderId())
                .productId(event.productId())
                .reason(ex.getMessage())
                .build());
    }

    public static BiFunction<ProductAction,ProductResponse,ProductActionDTO> toProductActionDTO(){
        return (action, dto) -> ProductActionDTO.builder()
                .action(action)
                .response(dto)
                .build();
    }

    public static ProductActionDTO productActionDTO(ProductAction action,String code){
        return ProductActionDTO.builder()
                .action(action)
                .response(ProductResponse.builder().code(code).build())
                .build();
    }

    public static Function<ProductActionDTO,ProductEvent> toViewedProductEvent(){
        return dto -> ProductEvent.View.builder()
                .code(dto.response().code())
                .build();
    }

    public static Function<ProductActionDTO, ProductEvent> toCreatedProductEvent(){
        return dto -> ProductEvent.Created.builder()
                .code(dto.response().code())
                .productId(dto.response().id())
                .price(dto.response().price())
                .build();
    }

    public static Function<ProductActionDTO, ProductEvent> toUpdatedProductEvent(){
        return dto -> ProductEvent.Updated.builder()
                .code(dto.response().code())
                .price(dto.response().price())
                .build();
    }

    public static Function<ProductActionDTO, ProductEvent> toDeletedProductEvent(){
        return dto -> ProductEvent.Deleted.builder()
                .code(dto.response().code())
                .build();
    }







}
