package com.example.fileprocessor.repository;

import com.example.fileprocessor.entity.ProcessedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessedFileRepository extends JpaRepository<ProcessedFile, Long> {
    Optional<ProcessedFile> findByFilename(String filename);
    List<ProcessedFile> findByStatus(String status);
    List<ProcessedFile> findAllByOrderByProcessedAtDesc();
}
