package com.example.ratingservice;

import com.example.ratingservice.repo.OrderHistoryRepo;
import com.example.ratingservice.repo.RatingRepo;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
            "logging.level.root=ERROR",
            "logging.level.com.example=INFO",
            "spring.cloud.stream.kafka.binder.configuration.auto.offset.reset=earliest"
        })
@DirtiesContext
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@AutoConfigureWebTestClient
public abstract class AbstractIntegrationTests {

    @Autowired
    protected WebTestClient client;

    @Autowired
    protected StreamBridge streamBridge;

    @Autowired
    protected OrderHistoryRepo orderRepo;

    @Autowired
    protected RatingRepo ratingRepo;

    @Autowired
    private DatabaseClient databaseClient;

    @Value("${test.timeout}")
    protected Long timeout;

    private static final String TEST_DATA =
            """
            truncate table order_history;
            truncate table rating;
            """;

    @BeforeEach
    void setupTestData() {
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
