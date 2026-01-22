package com.example.fileprocessor.batch.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CsvRecord {
    private String id;
    private String date;
    private Double amount;
}
