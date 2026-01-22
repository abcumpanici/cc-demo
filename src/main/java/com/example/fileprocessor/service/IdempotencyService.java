package com.example.fileprocessor.service;

import com.example.fileprocessor.entity.ProcessedFile;
import com.example.fileprocessor.repository.ProcessedFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final ProcessedFileRepository processedFileRepository;

    public boolean isFileProcessed(String filename) {
        return processedFileRepository.findByFilename(filename).isPresent();
    }

    public void markFileAsProcessed(String filename, String status, int recordCount) {
        ProcessedFile processedFile = ProcessedFile.builder()
                .filename(filename)
                .processedAt(LocalDateTime.now())
                .status(status)
                .recordCount(recordCount)
                .build();
        processedFileRepository.save(processedFile);
    }
}
