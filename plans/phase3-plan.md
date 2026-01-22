Here is Claude's plan:
╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌
 Kafka Integration for File Processing

 Overview

 Add Kafka consumer to trigger file processing jobs when notifications arrive on file-notifications topic.

 Files to Create

 1. Model

 src/main/java/com/example/fileprocessor/kafka/model/FileNotification.java
 - Fields: filename (String), timestamp (String)
 - Use Lombok @Data, @NoArgsConstructor, @AllArgsConstructor

 2. Kafka Configuration

 src/main/java/com/example/fileprocessor/kafka/config/KafkaConfig.java
 - ConsumerFactory<String, FileNotification> with JsonDeserializer
 - ConcurrentKafkaListenerContainerFactory bean
 - Configure trusted packages for deserialization

 3. Services

 src/main/java/com/example/fileprocessor/service/IdempotencyService.java
 - boolean isFileProcessed(String filename) - check ProcessedFileRepository.findByFilename()
 - void markFileAsProcessed(String filename, String status, int recordCount) - save to ProcessedFile

 src/main/java/com/example/fileprocessor/service/FileProcessingService.java
 - void processFile(FileNotification notification):
   a. Check idempotency (skip if already processed)
   b. Build input path from file.input.directory + filename
   c. Verify file exists
   d. Launch batch job with JobLauncher
   e. On success: mark as COMPLETED, move to processed
   f. On failure: mark as FAILED, move to failed

 4. Kafka Consumer

 src/main/java/com/example/fileprocessor/kafka/consumer/FileNotificationConsumer.java
 - @KafkaListener on file-notifications topic
 - Inject FileProcessingService
 - Log notification received, delegate to service

 Files to Modify

 application.yml

 Add JSON deserializer config:
 spring:
   kafka:
     consumer:
       value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
       properties:
         spring.json.trusted.packages: com.example.fileprocessor.kafka.model

 Verification

 1. mvn clean compile - build succeeds
 2. docker compose up -d - PostgreSQL and Kafka running
 3. mvn spring-boot:run - app starts
 4. Send test message to Kafka:
 docker exec -it fileprocessor-kafka kafka-console-producer.sh \
   --broker-list localhost:9092 --topic file-notifications
 # Enter: {"filename":"test.csv","timestamp":"2025-01-22T10:30:00Z"}
 5. Check logs for job execution
 6. Query processed_files and processed_records tables
╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌

 Would you like to proceed?

 ❯ 1. Yes, clear context and auto-accept edits (shift+tab)
   2. Yes, auto-accept edits
   3. Yes, manually approve edits
   4. Type here to tell Claude what to change

 ctrl-g to edit in VS Code · ~/.claude/plans/smooth-imagining-hartmanis.md
