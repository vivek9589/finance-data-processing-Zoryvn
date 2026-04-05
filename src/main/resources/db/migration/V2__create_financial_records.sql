
CREATE TABLE financial_records (
    id BINARY(16) NOT NULL PRIMARY KEY,
    amount DECIMAL(19,2) NOT NULL CHECK (amount > 0),
    type VARCHAR(50) NOT NULL,
    category VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    transaction_date DATE NOT NULL,
    user_id BINARY(16) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_financial_records_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_financial_records_type ON financial_records(type);
CREATE INDEX idx_financial_records_category ON financial_records(category);
CREATE INDEX idx_financial_records_user_id ON financial_records(user_id);
CREATE INDEX idx_financial_records_transaction_date ON financial_records(transaction_date);
