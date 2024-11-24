package com.example.order_service;

import com.example.order_service.repo.*;
import org.junit.jupiter.api.BeforeEach;
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
public abstract class AbstractIntegrationTests {

	@Autowired
	protected WebTestClient client;

	@Autowired
	protected StreamBridge streamBridge;

	@Autowired
	private DatabaseClient databaseClient;

	@Autowired
	protected ProductRepo productRepo;

	@Autowired
	protected PurchaseOrderRepo orderRepo;

	@Autowired
	protected InventoryRepo inventoryRepo;

	@Autowired
	protected ShippingRepo shippingRepo;

	@Autowired
	protected PaymentRepo paymentRepo;

	private static final String TEST_DATA = """
			TRUNCATE TABLE order_shipping, order_payment, order_inventory, purchase_order, product RESTART IDENTITY CASCADE;
			""";


	@BeforeEach
	void setupTestData() {
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
