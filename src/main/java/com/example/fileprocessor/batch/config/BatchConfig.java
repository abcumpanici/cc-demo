package com.example.fileprocessor.batch.config;

import com.example.fileprocessor.batch.dto.CsvRecord;
import com.example.fileprocessor.batch.listener.JobLoggingListener;
import com.example.fileprocessor.batch.processor.CsvRecordProcessor;
import com.example.fileprocessor.batch.skip.RecordSkipPolicy;
import com.example.fileprocessor.entity.ProcessedRecord;
import com.example.fileprocessor.repository.ProcessedRecordRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ProcessedRecordRepository processedRecordRepository;

    public BatchConfig(JobRepository jobRepository,
                       PlatformTransactionManager transactionManager,
                       ProcessedRecordRepository processedRecordRepository) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.processedRecordRepository = processedRecordRepository;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<CsvRecord> csvReader(
            @Value("#{jobParameters['inputFile']}") String inputFile) {
        return new FlatFileItemReaderBuilder<CsvRecord>()
                .name("csvReader")
                .resource(new FileSystemResource(inputFile))
                .delimited()
                .names("id", "date", "amount")
                .targetType(CsvRecord.class)
                .build();
    }

    @Bean
    @StepScope
    public CsvRecordProcessor csvProcessor() {
        return new CsvRecordProcessor();
    }

    @Bean
    public RepositoryItemWriter<ProcessedRecord> recordWriter() {
        return new RepositoryItemWriterBuilder<ProcessedRecord>()
                .repository(processedRecordRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public JobLoggingListener jobLoggingListener() {
        return new JobLoggingListener();
    }

    @Bean
    public RecordSkipPolicy recordSkipPolicy() {
        return new RecordSkipPolicy();
    }

    @Bean
    public Step processFileStep(FlatFileItemReader<CsvRecord> csvReader,
                                CsvRecordProcessor csvProcessor,
                                RepositoryItemWriter<ProcessedRecord> recordWriter) {
        return new StepBuilder("processFileStep", jobRepository)
                .<CsvRecord, ProcessedRecord>chunk(5, transactionManager)
                .reader(csvReader)
                .processor(csvProcessor)
                .writer(recordWriter)
                .faultTolerant()
                .skipPolicy(recordSkipPolicy())
                .listener(jobLoggingListener())
                .build();
    }

    @Bean
    public Job fileProcessingJob(Step processFileStep) {
        return new JobBuilder("fileProcessingJob", jobRepository)
                .listener(jobLoggingListener())
                .start(processFileStep)
                .build();
    }
}
