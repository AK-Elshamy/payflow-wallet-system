CREATE DATABASE payflow_db;

USE payflow_db;


CREATE TABLE users(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);


CREATE TABLE wallets(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    balance DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    currency VARCHAR(3) NOT NULL,
    CONSTRAINT fk_user 
    FOREIGN KEY(user_id) REFERENCES users(id),
    CONSTRAINT chk_balance CHECK (balance >= 0),
    CONSTRAINT chk_currency 
    CHECK (currency IN ('EGP', 'USD', 'EUR'))
);

CREATE TABLE transfers(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_wallet_id BIGINT NOT NULL,
    destination_wallet_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_source_wallet
    FOREIGN KEY(source_wallet_id) REFERENCES wallets(id),
    CONSTRAINT fk_destination_wallet
    FOREIGN KEY(destination_wallet_id) REFERENCES wallets(id),
    CONSTRAINT chk_amount CHECK (amount > 0),
    CONSTRAINT chk_transfer_status
    CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED'))
    
);

CREATE TABLE ledger_entries(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transfer_id BIGINT NOT NULL,
    wallet_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    created_at DATETIME NOT NULL,
    entry_type VARCHAR(20) NOT NULL,
    CONSTRAINT fk_transfer
    FOREIGN KEY(transfer_id) REFERENCES transfers(id),
    CONSTRAINT fk_wallet
    FOREIGN KEY(wallet_id) REFERENCES wallets(id),
    CONSTRAINT chk_amount_ledger CHECK (amount > 0),
    CONSTRAINT chk_entry_type CHECK (entry_type IN ('CREDIT', 'DEBIT'))
    ,CONSTRAINT uq_transfer_type
        UNIQUE(transfer_id, entry_type)
);