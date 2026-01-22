package com.example.fileprocessor.batch.skip;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileParseException;

@Slf4j
public class RecordSkipPolicy implements SkipPolicy {

    private static final int MAX_SKIP_COUNT = 100;

    @Override
    public boolean shouldSkip(Throwable t, long skipCount) throws SkipLimitExceededException {
        if (skipCount >= MAX_SKIP_COUNT) {
            throw new SkipLimitExceededException(MAX_SKIP_COUNT, t);
        }

        if (t instanceof FlatFileParseException e) {
            log.warn("Skipping invalid row at line {}: {}", e.getLineNumber(), e.getInput());
            return true;
        }

        if (t instanceof Exception) {
            log.warn("Skipping record due to error: {}", t.getMessage());
            return true;
        }

        return false;
    }
}
