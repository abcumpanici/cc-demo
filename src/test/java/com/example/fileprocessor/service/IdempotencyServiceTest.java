package com.example.fileprocessor.service;

import com.example.fileprocessor.entity.ProcessedFile;
import com.example.fileprocessor.repository.ProcessedFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdempotencyServiceTest {

    @Mock
    private ProcessedFileRepository processedFileRepository;

    private IdempotencyService idempotencyService;

    @BeforeEach
    void setUp() {
        idempotencyService = new IdempotencyService(processedFileRepository);
    }

    @Test
    void isFileProcessed_shouldReturnFalseForNewFile() {
        when(processedFileRepository.findByFilename("new-file.csv")).thenReturn(Optional.empty());

        boolean result = idempotencyService.isFileProcessed("new-file.csv");

        assertThat(result).isFalse();
        verify(processedFileRepository).findByFilename("new-file.csv");
    }

    @Test
    void isFileProcessed_shouldReturnTrueForExistingFile() {
        ProcessedFile existingFile = ProcessedFile.builder()
                .filename("existing-file.csv")
                .status("COMPLETED")
                .build();
        when(processedFileRepository.findByFilename("existing-file.csv")).thenReturn(Optional.of(existingFile));

        boolean result = idempotencyService.isFileProcessed("existing-file.csv");

        assertThat(result).isTrue();
        verify(processedFileRepository).findByFilename("existing-file.csv");
    }

    @Test
    void markFileAsProcessed_shouldSaveCorrectEntity() {
        when(processedFileRepository.save(any(ProcessedFile.class))).thenAnswer(i -> i.getArgument(0));

        idempotencyService.markFileAsProcessed("test.csv", "COMPLETED", 100);

        ArgumentCaptor<ProcessedFile> captor = ArgumentCaptor.forClass(ProcessedFile.class);
        verify(processedFileRepository).save(captor.capture());

        ProcessedFile saved = captor.getValue();
        assertThat(saved.getFilename()).isEqualTo("test.csv");
        assertThat(saved.getStatus()).isEqualTo("COMPLETED");
        assertThat(saved.getRecordCount()).isEqualTo(100);
        assertThat(saved.getProcessedAt()).isNotNull();
    }
}
