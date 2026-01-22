package com.example.fileprocessor.repository;

import com.example.fileprocessor.entity.ProcessedRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessedRecordRepository extends JpaRepository<ProcessedRecord, Long> {
    List<ProcessedRecord> findBySourceFilename(String sourceFilename);
}
