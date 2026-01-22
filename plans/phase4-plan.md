Ready to code?

 Here is Claude's plan:
╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌
 Phase 4: Tests and Scripts

 Files to Modify

 pom.xml

 Add Cucumber dependencies:
 <dependency>
     <groupId>io.cucumber</groupId>
     <artifactId>cucumber-java</artifactId>
     <version>7.15.0</version>
     <scope>test</scope>
 </dependency>
 <dependency>
     <groupId>io.cucumber</groupId>
     <artifactId>cucumber-spring</artifactId>
     <version>7.15.0</version>
     <scope>test</scope>
 </dependency>
 <dependency>
     <groupId>io.cucumber</groupId>
     <artifactId>cucumber-junit-platform-engine</artifactId>
     <version>7.15.0</version>
     <scope>test</scope>
 </dependency>
 <dependency>
     <groupId>org.junit.platform</groupId>
     <artifactId>junit-platform-suite</artifactId>
     <scope>test</scope>
 </dependency>

 Unit Tests

 src/test/java/com/example/fileprocessor/batch/processor/CsvRecordProcessorTest.java

 - Test CSV row conversion to ProcessedRecord entity
 - Test date parsing (yyyyMMdd format)
 - Test filename extraction from path

 src/test/java/com/example/fileprocessor/service/IdempotencyServiceTest.java

 - Mock ProcessedFileRepository
 - Test isFileProcessed() returns false for new file
 - Test isFileProcessed() returns true for existing file
 - Test markFileAsProcessed() saves correct entity

 src/test/java/com/example/fileprocessor/service/FileServiceTest.java

 - Use @TempDir for test directories
 - Test moveToProcessed() moves file correctly
 - Test moveToFailed() moves file correctly

 Integration Test

 src/test/java/com/example/fileprocessor/integration/FileProcessingIT.java

 - Use Testcontainers (PostgreSQL + Kafka)
 - Extend existing container setup pattern from FileProcessorApplicationTests
 - Test flow: create 10-row CSV → send Kafka message → verify 10 records in DB
 - Use KafkaTemplate to send message
 - Poll database with Awaitility for completion

 Cucumber Test

 src/test/resources/features/file-processing.feature

 Feature: File Processing
   Scenario: Process CSV file via Kafka notification
     Given a CSV file exists with 10 records
     When a file notification is received
     Then 10 records should be stored in database
     And file should be moved to processed folder

 src/test/java/com/example/fileprocessor/cucumber/CucumberIT.java

 - @Suite with @SelectClasspathResource("features")
 - @ConfigurationParameter for glue package

 src/test/java/com/example/fileprocessor/cucumber/FileProcessingSteps.java

 - @CucumberContextConfiguration with @SpringBootTest
 - Testcontainers setup
 - Step definitions matching feature file

 Performance Test

 src/test/java/com/example/fileprocessor/performance/FileProcessingPerformanceIT.java

 - Generate 50,000 row CSV file programmatically
 - Send Kafka notification
 - Measure total duration using System.nanoTime()
 - Calculate and log records/second
 - Use Awaitility to wait for completion

 Scripts

 scripts/generate-test-data.sh

 #!/bin/bash
 # Usage: ./generate-test-data.sh --rows 1000 --output test.csv
 # Generates CSV with columns: id,date,amount (yyyyMMdd format)

 scripts/send-notification.sh

 #!/bin/bash
 # Usage: ./send-notification.sh --filename test.csv
 # Sends JSON to file-notifications topic via docker

 Verification

 1. mvn test - all unit tests pass
 2. mvn verify -Pit - integration tests pass (requires Docker)
 3. ./scripts/generate-test-data.sh --rows 100 --output ./input/test.csv
 4. ./scripts/send-notification.sh --filename test.csv
 5. Check processed_files and processed_records tables
╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌

 Would you like to proceed?

 ❯ 1. Yes, clear context and auto-accept edits (shift+tab)
   2. Yes, auto-accept edits
   3. Yes, manually approve edits
   4. Type here to tell Claude what to change

 ctrl-g to edit in VS Code · ~/.claude/plans/smooth-imagining-hartmanis.md
