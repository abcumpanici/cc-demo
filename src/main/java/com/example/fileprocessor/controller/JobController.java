package com.example.fileprocessor.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController {

    private final JobLauncher jobLauncher;
    private final Job fileProcessingJob;

    public JobController(JobLauncher jobLauncher, Job fileProcessingJob) {
        this.jobLauncher = jobLauncher;
        this.fileProcessingJob = fileProcessingJob;
    }

    @PostMapping("/api/jobs/process-file")
    public ResponseEntity<String> runJob(@RequestParam String inputFile) {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("inputFile", inputFile)
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(fileProcessingJob, params);
            return ResponseEntity.ok("Job started for file: " + inputFile);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Job failed: " + e.getMessage());
        }
    }
}
