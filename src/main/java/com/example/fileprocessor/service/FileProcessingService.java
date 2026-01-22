package com.example.fileprocessor.service;

import com.example.fileprocessor.kafka.model.FileNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileProcessingService {

    private final IdempotencyService idempotencyService;
    private final FileService fileService;
    private final JobLauncher jobLauncher;
    private final Job fileProcessingJob;

    @Value("${file.input.directory}")
    private String inputDirectory;

    public void processFile(FileNotification notification) {
        String filename = notification.getFilename();
        log.info("Processing file notification: {}", filename);

        if (idempotencyService.isFileProcessed(filename)) {
            log.info("File {} already processed, skipping", filename);
            return;
        }

        Path inputPath = Path.of(inputDirectory, filename);

        if (!Files.exists(inputPath)) {
            log.error("File {} does not exist at {}", filename, inputPath);
            return;
        }

        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("inputFile", inputPath.toString())
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            var jobExecution = jobLauncher.run(fileProcessingJob, jobParameters);

            if (jobExecution.getStatus().isUnsuccessful()) {
                log.error("Job failed for file {}", filename);
                idempotencyService.markFileAsProcessed(filename, "FAILED", 0);
                fileService.moveToFailed(inputPath);
            } else {
                int recordCount = jobExecution.getStepExecutions().stream()
                        .mapToInt(step -> Math.toIntExact(step.getWriteCount()))
                        .sum();
                log.info("Job completed successfully for file {} with {} records", filename, recordCount);
                idempotencyService.markFileAsProcessed(filename, "COMPLETED", recordCount);
                fileService.moveToProcessed(inputPath);
            }
        } catch (Exception e) {
            log.error("Error processing file {}: {}", filename, e.getMessage(), e);
            try {
                idempotencyService.markFileAsProcessed(filename, "FAILED", 0);
                fileService.moveToFailed(inputPath);
            } catch (Exception moveException) {
                log.error("Failed to move file to failed directory: {}", moveException.getMessage());
            }
        }
    }
}
