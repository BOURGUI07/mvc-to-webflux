package com.example.shipping_service.repo;

import com.example.shipping_service.domain.Shipment;
import com.example.shipping_service.domain.ShippingStatus;
import io.netty.channel.unix.UnixChannelUtil;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ShippingRepo extends ReactiveCrudRepository<Shipment, UUID> {

    Mono<Boolean> existsByOrderId(UUID orderId);

    Mono<Shipment> findByOrderIdAndStatus(UUID orderId, ShippingStatus status);

    Mono<Shipment> findByOrderId(UUID orderId);
}
