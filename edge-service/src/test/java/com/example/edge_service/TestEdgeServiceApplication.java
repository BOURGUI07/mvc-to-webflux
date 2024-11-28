package com.example.edge_service;

import org.springframework.boot.SpringApplication;

public class TestEdgeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(EdgeServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
