package com.example.fileprocessor.batch.processor;

import com.example.fileprocessor.batch.dto.CsvRecord;
import com.example.fileprocessor.entity.ProcessedRecord;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CsvRecordProcessor implements ItemProcessor<CsvRecord, ProcessedRecord> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Value("#{jobParameters['inputFile']}")
    private String inputFile;

    @Override
    public ProcessedRecord process(CsvRecord item) throws Exception {
        LocalDate transactionDate = LocalDate.parse(item.getDate(), DATE_FORMATTER);
        BigDecimal amount = BigDecimal.valueOf(item.getAmount());

        String filename = inputFile;
        if (filename.contains("/")) {
            filename = filename.substring(filename.lastIndexOf("/") + 1);
        }

        return ProcessedRecord.builder()
                .transactionDate(transactionDate)
                .amount(amount)
                .sourceFilename(filename)
                .processedAt(LocalDateTime.now())
                .build();
    }
}
