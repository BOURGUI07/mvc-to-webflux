package com.example.catalog_service.integration_tests;


import java.util.Arrays;

import com.example.catalog_service.repo.ProductInventoryRepo;
import com.example.catalog_service.repo.ProductRepo;
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
public abstract class AbstractIntegrationTest {
    @Autowired
    protected WebTestClient client;

    @Autowired
    protected StreamBridge streamBridge;

    @Autowired
    protected ProductRepo productRepo;

    @Autowired
    protected ProductInventoryRepo inventoryRepo;

    @Autowired
    private DatabaseClient databaseClient;

    @Value("${test.timeout}")
    protected Long timeout;

    private static final String TEST_DATA =
            """
            truncate table product_inventory cascade;
            truncate table products cascade;

                    INSERT INTO products(code, name, description, image_url, price, available_quantity) VALUES        
                                ('P100', 'The Hunger Games', 'Winning will make you famous. Losing means certain death...', 'https://images.gr-assets.com/books/1447303603l/2767052.jpg', 34.0, 50),
                                ('P101', 'To Kill a Mockingbird', 'The unforgettable novel of a childhood in a sleepy Southern town and the crisis of conscience that rocked it...', 'https://images.gr-assets.com/books/1361975680l/2657.jpg', 45.40, 30),
                                ('P102', 'The Chronicles of Narnia', 'Journeys to the end of the world, fantastic creatures, and epic battles between good and evil—what more could any reader ask for in one book?...', 'https://images.gr-assets.com/books/1449868701l/11127.jpg', 44.50, 70),
                                ('P103', 'Gone with the Wind', 'Gone with the Wind is a novel written by Margaret Mitchell, first published in 1936.', 'https://images.gr-assets.com/books/1328025229l/18405.jpg', 44.50, 20),
                                ('P104', 'The Fault in Our Stars', 'Despite the tumor-shrinking medical miracle that has bought her a few years, Hazel has never been anything but terminal, her final chapter inscribed upon diagnosis.', 'https://images.gr-assets.com/books/1360206420l/11870085.jpg', 14.50, 15),
                                ('P105', 'The Giving Tree', 'Once there was a tree...and she loved a little boy.', 'https://images.gr-assets.com/books/1174210942l/370493.jpg', 32.0, 60),
                                ('P106', 'The Da Vinci Code', 'An ingenious code hidden in the works of Leonardo da Vinci. A desperate race through the cathedrals and castles of Europe', 'https://images.gr-assets.com/books/1303252999l/968.jpg', 14.50, 40),
                                ('P107', 'The Alchemist', 'Paulo Coelho''s masterpiece tells the mystical story of Santiago, an Andalusian shepherd boy who yearns to travel in search of a worldly treasure', 'https://images.gr-assets.com/books/1483412266l/865.jpg', 12.0, 25),
                                ('P108', 'Charlotte''s Web', 'This beloved book by E. B. White, author of Stuart Little and The Trumpet of the Swan, is a classic of children''s literature', 'https://images.gr-assets.com/books/1439632243l/24178.jpg', 14.0, 90),
                                ('P109', 'The Little Prince', 'Moral allegory and spiritual autobiography, The Little Prince is the most translated book in the French language.', 'https://images.gr-assets.com/books/1367545443l/157993.jpg', 16.50, 100),
                                ('P110', 'A Thousand Splendid Suns', 'A Thousand Splendid Suns is a breathtaking story set against the volatile events of Afghanistan''s last thirty years—from the Soviet invasion to the reign of the Taliban to post-Taliban rebuilding—that puts the violence, fear, hope, and faith of this country in intimate, human terms.', 'https://images.gr-assets.com/books/1345958969l/128029.jpg', 15.50, 10),
                                ('P111', 'A Game of Thrones', 'Here is the first volume in George R. R. Martin''s magnificent cycle of novels that includes A Clash of Kings and A Storm of Swords.', 'https://images.gr-assets.com/books/1436732693l/13496.jpg', 32.0, 80),
                                ('P112', 'The Book Thief', 'Nazi Germany. The country is holding its breath. Death has never been busier, and will be busier still. By her brother''s graveside, Liesel''s life is changed when she picks up a single object, partially hidden in the snow.', 'https://images.gr-assets.com/books/1522157426l/19063.jpg', 30.0, 35),
                                ('P113', 'One Flew Over the Cuckoo''s Nest', 'Tyrannical Nurse Ratched rules her ward in an Oregon State mental hospital with a strict and unbending routine, unopposed by her patients, who remain cowed by mind-numbing medication and the threat of electric shock therapy.', 'https://images.gr-assets.com/books/1516211014l/332613.jpg', 23.0, 55),
                                ('P114', 'Fifty Shades of Grey', 'When literature student Anastasia Steele goes to interview young entrepreneur Christian Grey, she encounters a man who is beautiful, brilliant, and intimidating.', 'https://images.gr-assets.com/books/1385207843l/10818853.jpg', 27.0, 45);
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
