CREATE TABLE processed_files (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL UNIQUE,
    processed_at TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    record_count INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE processed_records (
    id BIGSERIAL PRIMARY KEY,
    transaction_date DATE NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    source_filename VARCHAR(255) NOT NULL,
    processed_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_processed_records_source_filename ON processed_records(source_filename);
CREATE INDEX idx_processed_files_status ON processed_files(status);
