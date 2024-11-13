package com.example.order_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableR2dbcAuditing
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}