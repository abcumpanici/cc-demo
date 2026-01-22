package com.example.fileprocessor.controller;

import com.example.fileprocessor.repository.ProcessedFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final JobExplorer jobExplorer;
    private final ProcessedFileRepository processedFileRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Map<String, Object>> jobExecutions = new ArrayList<>();

        for (String jobName : jobExplorer.getJobNames()) {
            jobExplorer.getJobInstances(jobName, 0, 50).forEach(instance -> {
                jobExplorer.getJobExecutions(instance).forEach(execution -> {
                    String duration = calculateDuration(execution);
                    long writeCount = execution.getStepExecutions().stream()
                        .mapToLong(step -> step.getWriteCount())
                        .sum();
                    String tps = calculateTps(execution, writeCount);
                    jobExecutions.add(Map.of(
                        "id", execution.getId(),
                        "startTime", execution.getStartTime() != null ? execution.getStartTime().toString() : "-",
                        "endTime", execution.getEndTime() != null ? execution.getEndTime().toString() : "-",
                        "status", execution.getStatus().toString(),
                        "duration", duration,
                        "recordCount", writeCount,
                        "tps", tps
                    ));
                });
            });
        }

        jobExecutions.sort(Comparator.comparing((Map<String, Object> m) -> (Long) m.get("id")).reversed());

        model.addAttribute("jobExecutions", jobExecutions);
        model.addAttribute("processedFiles", processedFileRepository.findAllByOrderByProcessedAtDesc());

        return "dashboard";
    }

    private String calculateDuration(JobExecution execution) {
        if (execution.getStartTime() == null || execution.getEndTime() == null) {
            return "-";
        }
        Duration duration = Duration.between(execution.getStartTime(), execution.getEndTime());
        return String.format("%d.%03ds", duration.getSeconds(), duration.toMillisPart());
    }

    private String calculateTps(JobExecution execution, long recordCount) {
        if (execution.getStartTime() == null || execution.getEndTime() == null || recordCount == 0) {
            return "-";
        }
        Duration duration = Duration.between(execution.getStartTime(), execution.getEndTime());
        double seconds = duration.toMillis() / 1000.0;
        if (seconds == 0) {
            return "-";
        }
        double tps = recordCount / seconds;
        return String.format("%.1f", tps);
    }
}
