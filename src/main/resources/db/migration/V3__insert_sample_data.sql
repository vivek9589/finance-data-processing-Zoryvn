-- V2__insert_sample_data.sql

-- Insert sample users
INSERT INTO users (id, name, email, role, status, is_deleted, created_at, updated_at)
VALUES
    (UUID_TO_BIN(UUID()), 'Alice Admin', 'alice.admin@example.com', 'ADMIN', 'ACTIVE', FALSE, '2026-04-04 15:54:44', '2026-04-04 15:54:44'),
    (UUID_TO_BIN(UUID()), 'Bob Analyst', 'bob.analyst@example.com', 'ANALYST', 'ACTIVE', FALSE, '2026-04-04 15:54:54', '2026-04-04 15:54:54'),
    (UUID_TO_BIN(UUID()), 'Charlie Viewer', 'charlie.viewer@example.com', 'VIEWER', 'ACTIVE', FALSE, '2026-04-04 15:55:03', '2026-04-04 15:55:03');

-- Insert credentials (linking by user_id)
INSERT INTO user_credentials (user_id, password_hash, is_active)
SELECT id, '$2a$10$Cajovkdj.iEwnUAQI4NuOed3362qTUZHOtYfn882hcL/zWUU7ATo2', TRUE
FROM users WHERE email = 'alice.admin@example.com';

INSERT INTO user_credentials (user_id, password_hash, is_active)
SELECT id, '$2a$10$KAUpJXA/UHCzVq4QkBBL.ujPtA9sRVfSRnty47CEkNbAK2Cnn9SC.', TRUE
FROM users WHERE email = 'bob.analyst@example.com';

INSERT INTO user_credentials (user_id, password_hash, is_active)
SELECT id, '$2a$10$CJG2WgdOKybUgdvbxGsXbeE76uHNmtN6/dVbj0c5ynrEDfDMV.3G6', TRUE
FROM users WHERE email = 'charlie.viewer@example.com';

-- Insert sample financial records
INSERT INTO financial_records (id, amount, type, category, description, transaction_date, user_id, is_deleted, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), 1500.00, 'INCOME', 'SALARY', 'Monthly salary credit', '2026-04-01', id, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users WHERE email = 'alice.admin@example.com';

INSERT INTO financial_records (id, amount, type, category, description, transaction_date, user_id, is_deleted, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), 300.00, 'EXPENSE', 'FOOD', 'Team lunch', '2026-04-02', id, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users WHERE email = 'bob.analyst@example.com';

INSERT INTO financial_records (id, amount, type, category, description, transaction_date, user_id, is_deleted, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), 200.00, 'EXPENSE', 'TRAVEL', 'Cab fare', '2026-04-03', id, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users WHERE email = 'charlie.viewer@example.com';