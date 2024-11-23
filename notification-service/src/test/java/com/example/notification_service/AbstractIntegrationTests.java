package com.example.notification_service;

import com.example.notification_service.repo.CustomerRepo;
import com.example.notification_service.repo.OrderRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;

import java.util.Arrays;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "logging.level.root=ERROR",
                "logging.level.com.example=INFO",
                "spring.cloud.stream.kafka.binder.configuration.auto.offset.reset=earliest"
        })
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@AutoConfigureWebTestClient
public abstract class AbstractIntegrationTests {

    @Autowired
    protected CustomerRepo repo;

    @Autowired
    protected OrderRepo orderRepo;

    @Autowired
    protected StreamBridge streamBridge;

    @Autowired
    private DatabaseClient databaseClient;

    private static final String TEST_DATA= """
            truncate table orders;
            truncate table customer;
            """;

    @BeforeEach
    void setUp() {
        // Split and execute statements separately since R2DBC doesn't support multiple statements
        Arrays.stream(TEST_DATA.split(";"))
                .map(String::trim)
                .filter(sql -> !sql.isEmpty())
                .forEach(sql -> databaseClient
                        .sql(sql)
                        .fetch()
                        .rowsUpdated()
                        .as(StepVerifier::create)
                        .expectNextCount(1)
                        .verifyComplete());
    }



}
