package com.example.order_service.cache;

import static com.example.order_service.util.Constants.RedisKeys.PAYMENT_KEY;

import com.example.order_service.entity.OrderPayment;
import com.example.order_service.repo.PaymentRepo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCacheService implements CacheService<OrderPayment> {
    private final PaymentRepo repo;
    private final ReactiveHashOperations<String, String, OrderPayment> operations;

    @Scheduled(cron = "0 0 0 * * *")
    public void evictCache() {
        operations.delete(PAYMENT_KEY)
                .doOnError(ex -> log.error("Failed to evict cache: {}", ex.getMessage()))
                .subscribe();
    }

    @Override
    public Mono<OrderPayment> findById(UUID id) {
        return operations
                .get(PAYMENT_KEY, id.toString())
                .switchIfEmpty(repo.findByOrderId(id)
                        .flatMap(payment -> operations.put(PAYMENT_KEY, id.toString(), payment).thenReturn(payment)));
    }

    @Override
    public Mono<Long> doOnChanged(OrderPayment payment) {
        return operations.remove(PAYMENT_KEY, payment.getOrderId().toString());
    }
}
