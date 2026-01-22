package com.example.fileprocessor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "processed_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "source_filename", nullable = false)
    private String sourceFilename;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;
}
