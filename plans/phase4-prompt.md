# Phase 4: Tests and Scripts (Simplified)

```
Add tests and helper scripts.

UNIT TESTS (use JUnit 5 and Mockito):

RecordProcessorTest - test CSV row to entity conversion, test date parsing

IdempotencyServiceTest - test checking new file returns false, existing file returns true

FileServiceTest - test file moving with temp directories

INTEGRATION TEST:

Create FileProcessingIT using Testcontainers for PostgreSQL and Kafka.
Test: create CSV with 10 rows, send Kafka message, wait for completion, verify 10 records in database.

CUCUMBER TEST:

Create file-processing.feature with one scenario:
- Given a CSV file exists with 10 records
- When a file notification is received
- Then records should be stored in database
- And file should be moved to processed folder

Create CucumberIT runner and FileProcessingSteps class.

PERFORMANCE TEST:

Create FileProcessingPerformanceIT:
- Generate 50000 row CSV file
- Send notification
- Measure total duration and records per second
- Log results

SCRIPTS:

Create scripts/generate-test-data.sh:
- Accepts --rows and --output parameters
- Generates CSV with random valid data

Create scripts/send-notification.sh:
- Accepts --filename parameter
- Sends JSON message to Kafka topic using docker
```