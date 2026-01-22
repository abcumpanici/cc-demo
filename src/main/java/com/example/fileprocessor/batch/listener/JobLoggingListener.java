package com.example.fileprocessor.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

@Slf4j
public class JobLoggingListener implements JobExecutionListener, ChunkListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Job {} started at {}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStartTime());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Job {} finished with status {} at {}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus(),
                jobExecution.getEndTime());
    }

    @Override
    public void beforeChunk(ChunkContext context) {
    }

    @Override
    public void afterChunk(ChunkContext context) {
        long readCount = context.getStepContext().getStepExecution().getReadCount();
        long writeCount = context.getStepContext().getStepExecution().getWriteCount();
        log.debug("Chunk processed - Read: {}, Written: {}", readCount, writeCount);
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        log.error("Chunk error in step {}", context.getStepContext().getStepName());
    }
}
