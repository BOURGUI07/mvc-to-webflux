package com.example.order_service.config;

import com.example.order_service.entity.OrderInventory;
import com.example.order_service.entity.OrderPayment;
import com.example.order_service.entity.OrderShipping;
import com.example.order_service.entity.PurchaseOrder;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheConfig {

    @Bean
    public <T> ReactiveHashOperations<String, UUID, T> genericTemplate(
            ReactiveRedisConnectionFactory redisConnectionFactory, Class<T> type) {
        var template = new ReactiveRedisTemplate<>(
                redisConnectionFactory,
                RedisSerializationContext.<String, T>newSerializationContext(new StringRedisSerializer())
                        .hashKey(new  Jackson2JsonRedisSerializer<>(UUID.class))
                        .hashValue(new Jackson2JsonRedisSerializer<>(type))
                        .build());
        return template.opsForHash();
    }

    @Bean
    public ReactiveHashOperations<String, UUID, PurchaseOrder> orderTemplate(ReactiveRedisConnectionFactory factory) {
        return genericTemplate(factory, PurchaseOrder.class);
    }

    @Bean
    public ReactiveHashOperations<String, UUID, OrderInventory> inventoryTemplate(
            ReactiveRedisConnectionFactory factory) {
        return genericTemplate(factory, OrderInventory.class);
    }

    @Bean
    public ReactiveHashOperations<String, UUID, OrderShipping> shippingTemplate(
            ReactiveRedisConnectionFactory factory) {
        return genericTemplate(factory, OrderShipping.class);
    }

    @Bean
    public ReactiveHashOperations<String, UUID, OrderPayment> paymentTemplate(ReactiveRedisConnectionFactory factory) {
        return genericTemplate(factory, OrderPayment.class);
    }
}
