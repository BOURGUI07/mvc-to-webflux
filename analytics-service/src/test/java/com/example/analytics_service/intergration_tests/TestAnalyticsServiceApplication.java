package com.example.analytics_service.intergration_tests;

import com.example.analytics_service.AnalyticsServiceApplication;
import org.springframework.boot.SpringApplication;

public class TestAnalyticsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(AnalyticsServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
