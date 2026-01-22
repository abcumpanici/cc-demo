package com.example.fileprocessor.kafka.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileNotification {
    private String filename;
    private String timestamp;
}
