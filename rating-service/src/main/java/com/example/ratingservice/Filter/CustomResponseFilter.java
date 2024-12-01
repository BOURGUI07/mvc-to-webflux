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
    @NotNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        if(exchange.getRequest().getMethod().equals(HttpMethod.GET)){
            log.info("WebFilter :: Processing GET request: {}", exchange.getRequest().getURI());
            exchange.getResponse().getHeaders().set("Cache-Control", "max-age=60");
        }
        return chain.filter(exchange);
    }
}
