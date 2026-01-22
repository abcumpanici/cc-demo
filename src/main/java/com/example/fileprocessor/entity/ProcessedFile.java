package com.example.fileprocessor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String filename;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    @Column(nullable = false)
    private String status;

    @Column(name = "record_count", nullable = false)
    private Integer recordCount;
}
