
-- Create database
\c transaction_db

CREATE TABLE transactions (
    transaction_id VARCHAR(36) PRIMARY KEY,
    source_account VARCHAR(50) NOT NULL,
    destination_account VARCHAR(50) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL CHECK (amount > 0),
    timestamp TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    description VARCHAR(500),
    failure_reason VARCHAR(500),
    correlation_id VARCHAR(36),
    created_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);

-- Create indexes for optimized queries
CREATE INDEX idx_source_account ON transactions(source_account);
CREATE INDEX idx_destination_account ON transactions(destination_account);
CREATE INDEX idx_status ON transactions(status);
CREATE INDEX idx_timestamp ON transactions(timestamp);
CREATE INDEX idx_correlation_id ON transactions(correlation_id);

INSERT INTO public.transactions (transaction_id, source_account, destination_account, amount, timestamp, status, description, correlation_id, created_by, version)
VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 'ACC001', 'ACC002', 100.50, '2025-12-31 10:30:00', 'COMPLETED', 'Payment for services', '550e8400-e29b-41d4-a716-446655440011', 'john.doe@example.com', 1),
    ('550e8400-e29b-41d4-a716-446655440002', 'ACC002', 'ACC003', 250.00, '2025-12-31 09:15:00', 'COMPLETED', 'Invoice payment #12345', '550e8400-e29b-41d4-a716-446655440012', 'jane.smith@example.com', 1),
    ('550e8400-e29b-41d4-a716-446655440003', 'ACC003', 'ACC001', 500.75, '2025-12-30 14:20:00', 'COMPLETED', 'Refund for order #98765', '550e8400-e29b-41d4-a716-446655440013', 'admin@example.com', 1),
    ('550e8400-e29b-41d4-a716-446655440004', 'ACC001', 'ACC004', 1500.00, '2025-12-30 11:45:00', 'COMPLETED', 'Monthly rent payment', '550e8400-e29b-41d4-a716-446655440014', 'john.doe@example.com', 1),
    ('550e8400-e29b-41d4-a716-446655440005', 'ACC005', 'ACC001', 3500.00, '2025-12-29 16:30:00', 'COMPLETED', 'Salary - December 2025', '550e8400-e29b-41d4-a716-446655440015', 'payroll@example.com', 1);

-- Insert pending transactions
INSERT INTO public.transactions (transaction_id, source_account, destination_account, amount, timestamp, status, description, correlation_id, created_by, version)
VALUES
    ('550e8400-e29b-41d4-a716-446655440006', 'ACC006', 'ACC007', 750.00, '2025-12-31 11:00:00', 'PENDING', 'Business transaction - pending', '550e8400-e29b-41d4-a716-446655440016', 'business@example.com', 0),
    ('550e8400-e29b-41d4-a716-446655440007', 'ACC008', 'ACC009', 125.50, '2025-12-31 10:45:00', 'PENDING', 'Utility bill payment', '550e8400-e29b-41d4-a716-446655440017', 'user@example.com', 0);

-- Insert processing transactions
INSERT INTO public.transactions (transaction_id, source_account, destination_account, amount, timestamp, status, description, correlation_id, created_by, version)
VALUES
    ('550e8400-e29b-41d4-a716-446655440008', 'ACC010', 'ACC011', 2000.00, '2025-12-31 09:30:00', 'PROCESSING', 'Large transfer - processing', '550e8400-e29b-41d4-a716-446655440018', 'vip@example.com', 1),
    ('550e8400-e29b-41d4-a716-446655440009', 'ACC012', 'ACC013', 89.99, '2025-12-31 08:15:00', 'PROCESSING', 'Online purchase payment', '550e8400-e29b-41d4-a716-446655440019', 'customer@example.com', 1);

-- Insert failed transactions
INSERT INTO public.transactions (transaction_id, source_account, destination_account, amount, timestamp, status, description, failure_reason, correlation_id, created_by, version)
VALUES
    ('550e8400-e29b-41d4-a716-446655440010', 'ACC014', 'ACC015', 5000.00, '2025-12-30 15:30:00', 'FAILED', 'Large withdrawal attempt', 'Insufficient balance in source account', '550e8400-e29b-41d4-a716-446655440020', 'user1@example.com', 2),
    ('550e8400-e29b-41d4-a716-446655440011', 'ACC016', 'ACC017', 300.00, '2025-12-30 12:00:00', 'FAILED', 'Transfer to closed account', 'Destination account is not active', '550e8400-e29b-41d4-a716-446655440021', 'user2@example.com', 2),
    ('550e8400-e29b-41d4-a716-446655440012', 'ACC018', 'ACC019', 150.00, '2025-12-29 14:15:00', 'FAILED', 'Invalid account transfer', 'Source account not found', '550e8400-e29b-41d4-a716-446655440022', 'user3@example.com', 2);

-- Insert compensating transactions (Saga rollback in progress)
INSERT INTO public.transactions (transaction_id, source_account, destination_account, amount, timestamp, status, description, failure_reason, correlation_id, created_by, version)
VALUES
    ('550e8400-e29b-41d4-a716-446655440013', 'ACC020', 'ACC021', 450.00, '2025-12-29 10:00:00', 'COMPENSATING', 'Reversal in progress', 'Customer requested cancellation', '550e8400-e29b-41d4-a716-446655440023', 'support@example.com', 3);

-- Insert compensated transactions (Saga rollback completed)
INSERT INTO public.transactions (transaction_id, source_account, destination_account, amount, timestamp, status, description, failure_reason, correlation_id, created_by, version)
VALUES
    ('550e8400-e29b-41d4-a716-446655440014', 'ACC022', 'ACC023', 275.25, '2025-12-28 16:30:00', 'COMPENSATED', 'Reversed transaction', 'Duplicate transaction detected', '550e8400-e29b-41d4-a716-446655440024', 'system@example.com', 4),
    ('550e8400-e29b-41d4-a716-446655440015', 'ACC024', 'ACC025', 600.00, '2025-12-27 13:45:00', 'COMPENSATED', 'Cancelled transfer', 'User cancelled before completion', '550e8400-e29b-41d4-a716-446655440025', 'support@example.com', 4);

-- Insert transactions for specific account history testing (ACC001)
INSERT INTO public.transactions (transaction_id, source_account, destination_account, amount, timestamp, status, description, correlation_id, created_by, version)
VALUES
    ('550e8400-e29b-41d4-a716-446655440016', 'ACC001', 'ACC026', 75.00, '2025-12-27 10:00:00', 'COMPLETED', 'Small payment test', '550e8400-e29b-41d4-a716-446655440026', 'john.doe@example.com', 1),
    ('550e8400-e29b-41d4-a716-446655440017', 'ACC027', 'ACC001', 200.00, '2025-12-26 15:30:00', 'COMPLETED', 'Incoming payment test', '550e8400-e29b-41d4-a716-446655440027', 'sender@example.com', 1),
    ('550e8400-e29b-41d4-a716-446655440018', 'ACC001', 'ACC028', 0.01, '2025-12-25 09:00:00', 'COMPLETED', 'Minimum amount test', '550e8400-e29b-41d4-a716-446655440028', 'john.doe@example.com', 1);

-- Insert edge case transactions
INSERT INTO public.transactions (transaction_id, source_account, destination_account, amount, timestamp, status, description, correlation_id, created_by, version)
VALUES
    ('550e8400-e29b-41d4-a716-446655440019', 'ACC100', 'ACC101', 0.01, '2025-12-31 07:00:00', 'COMPLETED', 'Minimum amount edge case', '550e8400-e29b-41d4-a716-446655440029', 'test@example.com', 1),
    ('550e8400-e29b-41d4-a716-446655440020', 'ACC102', 'ACC103', 99999999.99, '2025-12-31 06:00:00', 'COMPLETED', 'Maximum amount edge case', '550e8400-e29b-41d4-a716-446655440030', 'whale@example.com', 1),
    ('550e8400-e29b-41d4-a716-446655440021', 'ACC104', 'ACC105', 1234.56, '2025-12-24 12:00:00', 'COMPLETED', 'Transaction without description test', '550e8400-e29b-41d4-a716-446655440031', 'user@example.com', 1);

-- Insert transactions for bulk testing (same correlation ID)
INSERT INTO public.transactions (transaction_id, source_account, destination_account, amount, timestamp, status, description, correlation_id, created_by, version)
VALUES
    ('550e8400-e29b-41d4-a716-446655440022', 'ACC200', 'ACC201', 100.00, '2025-12-23 10:00:00', 'COMPLETED', 'Batch transfer 1', 'batch-550e8400-e29b-41d4-a716', 'batch@example.com', 1),
    ('550e8400-e29b-41d4-a716-446655440023', 'ACC200', 'ACC202', 100.00, '2025-12-23 10:00:01', 'COMPLETED', 'Batch transfer 2', 'batch-550e8400-e29b-41d4-a716', 'batch@example.com', 1),
    ('550e8400-e29b-41d4-a716-446655440024', 'ACC200', 'ACC203', 100.00, '2025-12-23 10:00:02', 'COMPLETED', 'Batch transfer 3', 'batch-550e8400-e29b-41d4-a716', 'batch@example.com', 1);


