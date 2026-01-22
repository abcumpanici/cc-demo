package com.example.fileprocessor.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class FileServiceTest {

    @TempDir
    Path tempDir;

    private FileService fileService;
    private Path processedDir;
    private Path failedDir;

    @BeforeEach
    void setUp() {
        fileService = new FileService();
        processedDir = tempDir.resolve("processed");
        failedDir = tempDir.resolve("failed");
        ReflectionTestUtils.setField(fileService, "processedDirectory", processedDir.toString());
        ReflectionTestUtils.setField(fileService, "failedDirectory", failedDir.toString());
    }

    @Test
    void moveToProcessed_shouldMoveFileCorrectly() throws Exception {
        Path sourceFile = tempDir.resolve("test.csv");
        Files.writeString(sourceFile, "id,date,amount\n1,20240101,100.0");

        fileService.moveToProcessed(sourceFile);

        assertThat(sourceFile).doesNotExist();
        assertThat(processedDir.resolve("test.csv")).exists();
        assertThat(Files.readString(processedDir.resolve("test.csv"))).isEqualTo("id,date,amount\n1,20240101,100.0");
    }

    @Test
    void moveToFailed_shouldMoveFileCorrectly() throws Exception {
        Path sourceFile = tempDir.resolve("error.csv");
        Files.writeString(sourceFile, "invalid content");

        fileService.moveToFailed(sourceFile);

        assertThat(sourceFile).doesNotExist();
        assertThat(failedDir.resolve("error.csv")).exists();
        assertThat(Files.readString(failedDir.resolve("error.csv"))).isEqualTo("invalid content");
    }

    @Test
    void moveToProcessed_shouldCreateDirectoryIfNotExists() throws Exception {
        Path sourceFile = tempDir.resolve("data.csv");
        Files.writeString(sourceFile, "test content");

        assertThat(processedDir).doesNotExist();

        fileService.moveToProcessed(sourceFile);

        assertThat(processedDir).exists();
        assertThat(processedDir.resolve("data.csv")).exists();
    }
}
