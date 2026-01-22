package com.example.fileprocessor.cucumber;

import com.example.fileprocessor.kafka.model.FileNotification;
import com.example.fileprocessor.repository.ProcessedFileRepository;
import com.example.fileprocessor.repository.ProcessedRecordRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@CucumberContextConfiguration
@SpringBootTest(properties = {
    "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer"
})
public class FileProcessingSteps {

    static PostgreSQLContainer<?> postgres;
    static KafkaContainer kafka;

    static {
        postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16"));
        postgres.start();

        kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));
        kafka.start();
    }

    @Autowired
    private KafkaTemplate<String, FileNotification> kafkaTemplate;

    @Autowired
    private ProcessedRecordRepository processedRecordRepository;

    @Autowired
    private ProcessedFileRepository processedFileRepository;

    @Value("${file.input.directory}")
    private String inputDirectory;

    @Value("${file.processed.directory}")
    private String processedDirectory;

    private String testFilename;
    private int expectedRecordCount;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Before
    public void setUp() {
        processedRecordRepository.deleteAll();
        processedFileRepository.deleteAll();
    }

    @Given("a CSV file exists with {int} records")
    public void aCsvFileExistsWithRecords(int recordCount) throws Exception {
        expectedRecordCount = recordCount;
        testFilename = "cucumber-test-" + Instant.now().toEpochMilli() + ".csv";
        Path inputPath = Path.of(inputDirectory);
        Files.createDirectories(inputPath);

        StringBuilder csv = new StringBuilder("id,date,amount\n");
        for (int i = 1; i <= recordCount; i++) {
            csv.append(i).append(",20240115,").append(i * 10.0).append("\n");
        }
        Files.writeString(inputPath.resolve(testFilename), csv.toString());
    }

    @When("a file notification is received")
    public void aFileNotificationIsReceived() {
        FileNotification notification = new FileNotification(testFilename, Instant.now().toString());
        kafkaTemplate.send("file-notifications", notification);
    }

    @Then("{int} records should be stored in database")
    public void recordsShouldBeStoredInDatabase(int expectedCount) {
        await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
            assertThat(processedRecordRepository.findBySourceFilename(testFilename)).hasSize(expectedCount);
        });
    }

    @Then("file should be moved to processed folder")
    public void fileShouldBeMovedToProcessedFolder() {
        Path processedPath = Path.of(processedDirectory).resolve(testFilename);
        Path inputPath = Path.of(inputDirectory).resolve(testFilename);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            assertThat(processedPath).exists();
            assertThat(inputPath).doesNotExist();
        });
    }
}
