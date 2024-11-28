package com.example.catalog_service.config;


import com.example.catalog_service.domain.Product;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheConfig {
    private <T> ReactiveHashOperations<String, String, T> genericTemplate(
            ReactiveRedisConnectionFactory redisConnectionFactory, Class<T> type) {
        var template = new ReactiveRedisTemplate<>(
                redisConnectionFactory,
                RedisSerializationContext.<String, T>newSerializationContext(new StringRedisSerializer())
                        .hashKey(new StringRedisSerializer())
                        .hashValue(new Jackson2JsonRedisSerializer<>(type))
                        .build());
        return template.opsForHash();
    }

    @Bean
    public ReactiveHashOperations<String, String, Product> orderTemplate(ReactiveRedisConnectionFactory factory) {
        return genericTemplate(factory, Product.class);
    }
}
