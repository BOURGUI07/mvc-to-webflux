package com.example.analytics_service;

import com.example.analytics_service.dto.ProductViewDTO;
import com.example.analytics_service.events.ProductEvent;
import com.example.analytics_service.repo.ProductViewRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
	private WebTestClient client;

	@Autowired
	private ProductViewRepo repo;

	@Autowired
	private StreamBridge streamBridge;


	protected void viewProduct(String code, Integer howManyTimes) {
		var event = ProductEvent.View.builder().code(code).build();

		Flux.range(1,howManyTimes)
				.map(__ -> streamBridge.send("catalog-events",event))
				.then()
				.as(StepVerifier::create)
				.verifyComplete();
	}


	protected void testProductView(){
		client.get()
				.uri("/api/analytics/views")
				.exchange()
				.returnResult(ProductViewDTO.class)
				.getResponseBody()
				.take(2)
				.as(StepVerifier::create)
				.assertNext(dto -> assertEquals(22,dto.viewCount()))
				.assertNext(dto -> assertEquals(7,dto.viewCount()))
				.verifyComplete();
	}
}
