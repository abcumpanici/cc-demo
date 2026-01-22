package com.example.fileprocessor.kafka.consumer;

import com.example.fileprocessor.kafka.model.FileNotification;
import com.example.fileprocessor.service.FileProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileNotificationConsumer {

    private final FileProcessingService fileProcessingService;

    @KafkaListener(topics = "file-notifications", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(FileNotification notification) {
        log.info("Received file notification: filename={}, timestamp={}",
                notification.getFilename(), notification.getTimestamp());
        fileProcessingService.processFile(notification);
    }
}
