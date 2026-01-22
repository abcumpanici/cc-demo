package com.example.fileprocessor.performance;

import com.example.fileprocessor.kafka.model.FileNotification;
import com.example.fileprocessor.repository.ProcessedFileRepository;
import com.example.fileprocessor.repository.ProcessedRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
class FileProcessingPerformanceIT {

    private static final Logger log = LoggerFactory.getLogger(FileProcessingPerformanceIT.class);
    private static final int RECORD_COUNT = 50_000;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16"));

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @TempDir
    static Path tempDir;

    @Autowired
    private KafkaTemplate<String, FileNotification> kafkaTemplate;

    @Autowired
    private ProcessedRecordRepository processedRecordRepository;

    @Autowired
    private ProcessedFileRepository processedFileRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("file.input.directory", () -> tempDir.resolve("input").toString());
        registry.add("file.processed.directory", () -> tempDir.resolve("processed").toString());
        registry.add("file.failed.directory", () -> tempDir.resolve("failed").toString());
        registry.add("spring.kafka.producer.value-serializer", () -> "org.springframework.kafka.support.serializer.JsonSerializer");
    }

    @BeforeEach
    void setUp() throws Exception {
        processedRecordRepository.deleteAll();
        processedFileRepository.deleteAll();
        Files.createDirectories(tempDir.resolve("input"));
    }

    @Test
    void shouldProcessLargeFileWithinAcceptableTime() throws Exception {
        String filename = "performance-test-" + Instant.now().toEpochMilli() + ".csv";
        Path inputFile = tempDir.resolve("input").resolve(filename);

        log.info("Generating CSV file with {} records...", RECORD_COUNT);
        StringBuilder csv = new StringBuilder("id,date,amount\n");
        for (int i = 1; i <= RECORD_COUNT; i++) {
            csv.append(i).append(",20240115,").append(i % 1000 + 0.50).append("\n");
        }
        Files.writeString(inputFile, csv.toString());
        log.info("CSV file generated: {}", inputFile);

        FileNotification notification = new FileNotification(filename, Instant.now().toString());

        long startTime = System.nanoTime();
        kafkaTemplate.send("file-notifications", notification);

        await().atMost(Duration.ofMinutes(5)).untilAsserted(() -> {
            assertThat(processedRecordRepository.findBySourceFilename(filename)).hasSize(RECORD_COUNT);
        });
        long endTime = System.nanoTime();

        double durationSeconds = (endTime - startTime) / 1_000_000_000.0;
        double recordsPerSecond = RECORD_COUNT / durationSeconds;

        log.info("Performance Results:");
        log.info("  Total records: {}", RECORD_COUNT);
        log.info("  Total duration: {} seconds", String.format("%.2f", durationSeconds));
        log.info("  Records per second: {}", String.format("%.2f", recordsPerSecond));

        assertThat(processedFileRepository.findByFilename(filename)).isPresent();
        assertThat(processedFileRepository.findByFilename(filename).get().getRecordCount()).isEqualTo(RECORD_COUNT);
    }
}
