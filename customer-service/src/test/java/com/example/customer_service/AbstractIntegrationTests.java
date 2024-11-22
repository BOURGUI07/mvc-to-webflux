package com.example.customer_service;

import com.example.customer_service.repo.CustomerRepo;
import com.example.customer_service.repo.PaymentRepo;
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
import org.springframework.test.web.reactive.server.WebTestClient;
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
public class AbstractIntegrationTests {

	@Autowired
	protected WebTestClient client;

	@Autowired
	protected StreamBridge streamBridge;

	@Autowired
	protected CustomerRepo customerRepo;

	@Autowired
	protected PaymentRepo repo;

	@Autowired
	private DatabaseClient databaseClient;

	private static final String TEST_DATA = """
			truncate table customer_payment cascade;
			truncate table customer cascade;
			
			INSERT INTO customer (username, balance, email, phone, street, city, country, state)
			VALUES
			    ('john_doe', 2500, 'john.doe@example.com', '1234567890', '123 Main St', 'Springfield', 'USA', 'IL'),
			    ('jane_smith', 3200, 'jane.smith@example.com', '0987654321', '456 Elm St', 'Shelbyville', 'USA', 'TX'),
			    ('alice_johnson', 4000, 'alice.johnson@example.com', '5551234567', '789 Oak St', 'Metropolis', 'USA', 'NY'),
			    ('bob_brown', 4700, 'bob.brown@example.com', '1112223333', '101 Pine St', 'Gotham', 'USA', 'CA'),
			    ('eve_white', 2800, 'eve.white@example.com', '4445556666', '202 Cedar St', 'Star City', 'USA', 'FL');
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
