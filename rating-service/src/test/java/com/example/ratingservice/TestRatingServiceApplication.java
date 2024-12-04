package com.example.ratingservice;

import org.springframework.boot.SpringApplication;

public class TestRatingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(RatingServiceApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
