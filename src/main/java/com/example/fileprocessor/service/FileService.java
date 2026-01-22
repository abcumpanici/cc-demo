package com.example.fileprocessor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
public class FileService {

    @Value("${file.processed.directory}")
    private String processedDirectory;

    @Value("${file.failed.directory}")
    private String failedDirectory;

    public void moveToProcessed(Path sourceFile) throws IOException {
        moveFile(sourceFile, Path.of(processedDirectory));
    }

    public void moveToFailed(Path sourceFile) throws IOException {
        moveFile(sourceFile, Path.of(failedDirectory));
    }

    private void moveFile(Path sourceFile, Path targetDirectory) throws IOException {
        Files.createDirectories(targetDirectory);
        Path targetFile = targetDirectory.resolve(sourceFile.getFileName());
        Files.move(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
        log.info("Moved file {} to {}", sourceFile, targetFile);
    }
}
