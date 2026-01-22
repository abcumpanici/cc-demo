# Phase 2: File Processing Job (Simplified)

```
Add a Spring Batch job to read CSV files and store data in PostgreSQL.

CSV format (no header): id (long), date (YYYYMMDD), amount (double)
Example row: 1,20250122,1234.56

Create Flyway migration V1__create_tables.sql with:
- Table processed_records: id, transaction_date, amount, source_filename, processed_at
- Table processed_files: id, filename (unique), processed_at, status, record_count

Create JPA entities ProcessedRecord and ProcessedFile with repositories.

Create Spring Batch job config:
- Job name: fileProcessingJob
- Reader: FlatFileItemReader for CSV
- Processor: converts CSV row to ProcessedRecord entity
- Writer: saves to database
- Chunk size: 5
- Add logging listener for job start, chunk progress, and job end

Create FileService with methods:
- moveToProcessed(filename) - moves file to processed folder
- moveToFailed(filename) - moves file to failed folder

Invalid rows should be logged and ignored, processing continues.
```