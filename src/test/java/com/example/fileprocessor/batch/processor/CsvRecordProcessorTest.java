package com.example.fileprocessor.batch.processor;

import com.example.fileprocessor.batch.dto.CsvRecord;
import com.example.fileprocessor.entity.ProcessedRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CsvRecordProcessorTest {

    private CsvRecordProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new CsvRecordProcessor();
        ReflectionTestUtils.setField(processor, "inputFile", "/input/test.csv");
    }

    @Test
    void shouldConvertCsvRecordToProcessedRecord() throws Exception {
        CsvRecord csvRecord = new CsvRecord("1", "20240115", 100.50);

        ProcessedRecord result = processor.process(csvRecord);

        assertThat(result.getTransactionDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100.50));
        assertThat(result.getSourceFilename()).isEqualTo("test.csv");
        assertThat(result.getProcessedAt()).isNotNull();
    }

    @Test
    void shouldParseDateInYyyyMMddFormat() throws Exception {
        CsvRecord csvRecord = new CsvRecord("1", "20231225", 50.0);

        ProcessedRecord result = processor.process(csvRecord);

        assertThat(result.getTransactionDate()).isEqualTo(LocalDate.of(2023, 12, 25));
    }

    @Test
    void shouldExtractFilenameFromPath() throws Exception {
        ReflectionTestUtils.setField(processor, "inputFile", "/some/nested/path/data.csv");
        CsvRecord csvRecord = new CsvRecord("1", "20240101", 10.0);

        ProcessedRecord result = processor.process(csvRecord);

        assertThat(result.getSourceFilename()).isEqualTo("data.csv");
    }

    @Test
    void shouldHandleFilenameWithoutPath() throws Exception {
        ReflectionTestUtils.setField(processor, "inputFile", "simple.csv");
        CsvRecord csvRecord = new CsvRecord("1", "20240101", 10.0);

        ProcessedRecord result = processor.process(csvRecord);

        assertThat(result.getSourceFilename()).isEqualTo("simple.csv");
    }
}
