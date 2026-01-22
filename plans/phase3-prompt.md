# Phase 3: Kafka Integration (Simplified)

```
Add Kafka consumer to start the file processing job when a notification arrives.

Topic name: file-notifications

Message format (JSON):
{
  "filename": "batch_001.csv",
  "timestamp": "2025-01-22T10:30:00Z"
}

Create FileNotification model class with filename and timestamp fields.

Create KafkaConfig with consumer factory for JSON deserialization.

Create FileNotificationConsumer that listens to the topic and calls FileProcessingService.

Create IdempotencyService:
- isFileProcessed(filename) - checks if file exists in processed_files table
- markFileAsProcessed(filename, status, recordCount) - saves record

Create FileProcessingService:
- processFile(notification) method that:
  1. Checks if file was already processed, if yes log and return
  2. Verifies file exists at input path
  3. Launches the Spring Batch job
  4. Updates processed_files table
  5. Moves file to processed or failed folder

Add logging throughout: notification received, file already processed, job started, job completed.
```