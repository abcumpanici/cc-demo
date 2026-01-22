Ready to code?

 Here is Claude's plan:
╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌
 Spring Batch File Processing Job Implementation

 Files to Create

 1. Flyway Migration

 src/main/resources/db/migration/V1__create_tables.sql
 CREATE TABLE processed_files (
     id BIGSERIAL PRIMARY KEY,
     filename VARCHAR(255) NOT NULL UNIQUE,
     processed_at TIMESTAMP NOT NULL,
     status VARCHAR(50) NOT NULL,
     record_count INTEGER NOT NULL DEFAULT 0
 );

 CREATE TABLE processed_records (
     id BIGSERIAL PRIMARY KEY,
     transaction_date DATE NOT NULL,
     amount DECIMAL(19,2) NOT NULL,
     source_filename VARCHAR(255) NOT NULL,
     processed_at TIMESTAMP NOT NULL
 );

 CREATE INDEX idx_processed_records_source_filename ON processed_records(source_filename);
 CREATE INDEX idx_processed_files_status ON processed_files(status);

 2. Entities

 - src/main/java/com/example/fileprocessor/entity/ProcessedRecord.java - id, transactionDate, amount, sourceFilename, processedAt
 - src/main/java/com/example/fileprocessor/entity/ProcessedFile.java - id, filename, processedAt, status, recordCount

 3. Repositories

 - src/main/java/com/example/fileprocessor/repository/ProcessedRecordRepository.java
 - src/main/java/com/example/fileprocessor/repository/ProcessedFileRepository.java

 4. Batch Components

 - src/main/java/com/example/fileprocessor/batch/dto/CsvRecord.java - DTO with id, date (String), amount (Double)
 - src/main/java/com/example/fileprocessor/batch/config/BatchConfig.java - Job, Step, Reader, Writer beans
 - src/main/java/com/example/fileprocessor/batch/processor/CsvRecordProcessor.java - Converts CsvRecord to ProcessedRecord
 - src/main/java/com/example/fileprocessor/batch/skip/RecordSkipPolicy.java - Logs and skips invalid rows
 - src/main/java/com/example/fileprocessor/batch/listener/JobLoggingListener.java - Logs job start/end, chunk progress

 5. Service

 - src/main/java/com/example/fileprocessor/service/FileService.java - moveToProcessed(), moveToFailed()

 6. Configuration Update

 Add to application.yml:
 file:
   input:
     directory: ./input
   processed:
     directory: ./processed
   failed:
     directory: ./failed

 Key Implementation Details

 - Reader: FlatFileItemReader with job parameter inputFile for dynamic path
 - Processor: Parse YYYYMMDD → LocalDate, amount → BigDecimal, set sourceFilename and processedAt
 - Writer: RepositoryItemWriter using ProcessedRecordRepository
 - Skip Policy: Catch FlatFileParseException and log, return true to continue
 - Chunk size: 5
 - Job name: fileProcessingJob

 Verification

 1. mvn clean compile - build succeeds
 2. docker compose up -d - PostgreSQL running
 3. mvn spring-boot:run - app starts, Flyway creates tables
 4. Create test CSV input/test.csv:
 1,20250122,1234.56
 2,20250123,789.00
 5. Verify tables exist in PostgreSQL via pgAdmin (localhost:5050)
╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌

 Would you like to proceed?

 ❯ 1. Yes, clear context and auto-accept edits (shift+tab)
   2. Yes, auto-accept edits
   3. Yes, manually approve edits
   4. Type here to tell Claude what to change

 ctrl-g to edit in VS Code · ~/.claude/plans/smooth-imagining-hartmanis.md
