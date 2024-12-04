package com.example.ratingservice.Filter;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CustomResponseFilter implements WebFilter {
    @NotNull @Override
    public Mono<Void> filter(ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Log Swagger UI requests without interrupting
        if (path.contains("/swagger-ui") || path.contains("/v3/api-docs")) {
            log.info("WebFilter :: Swagger UI or API Docs request: {}", path);
        }

        // Add caching for GET requests, excluding Swagger resources
        if (exchange.getRequest().getMethod() == HttpMethod.GET
                && !path.contains("/swagger-ui")
                && !path.contains("/v3/api-docs")) {
            log.info("WebFilter :: Processing cacheable GET request: {}", path);
            exchange.getResponse().getHeaders().set("Cache-Control", "max-age=60");
        }

        return chain.filter(exchange);
    }
}
