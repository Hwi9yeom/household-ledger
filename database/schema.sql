-- Household Ledger Database Schema
-- Generated from JPA Entity Models

-- Drop tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS budget_alerts;
DROP TABLE IF EXISTS financial_goals;
DROP TABLE IF EXISTS recurring_transactions;
DROP TABLE IF EXISTS budgets;
DROP TABLE IF EXISTS ledger_entries;
DROP TABLE IF EXISTS expenses;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

-- Create users table
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255),
    google_id VARCHAR(255) UNIQUE,
    auth_provider VARCHAR(50) NOT NULL DEFAULT 'GOOGLE' CHECK (auth_provider IN ('GOOGLE', 'LOCAL')),
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create categories table
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE', 'SAVING_INVESTMENT')),
    sub_type VARCHAR(100) NOT NULL,
    icon VARCHAR(255),
    color VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_categories_user_id (user_id),
    INDEX idx_categories_type (type)
);

-- Create expenses table (simple expense tracking)
CREATE TABLE expenses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(255) NOT NULL,
    amount INT NOT NULL,
    category VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create ledger_entries table
CREATE TABLE ledger_entries (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    entry_type VARCHAR(50) NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    category_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    date DATE NOT NULL,
    description VARCHAR(255) NOT NULL,
    memo TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    INDEX idx_ledger_entries_user_id (user_id),
    INDEX idx_ledger_entries_category_id (category_id),
    INDEX idx_ledger_entries_date (date)
);

-- Create budgets table
CREATE TABLE budgets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    category_id BIGINT,  -- nullable for overall budget
    period VARCHAR(50) NOT NULL CHECK (period IN ('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY')),
    amount DECIMAL(19,2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_budgets_user_id (user_id),
    INDEX idx_budgets_category_id (category_id),
    INDEX idx_budgets_dates (start_date, end_date)
);

-- Create recurring_transactions table
CREATE TABLE recurring_transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    frequency VARCHAR(50) NOT NULL CHECK (frequency IN ('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY')),
    day_of_period INT NOT NULL CHECK (day_of_period >= 1 AND day_of_period <= 31),
    start_date DATE NOT NULL,
    end_date DATE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_processed_date DATE,
    next_scheduled_date DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    INDEX idx_recurring_transactions_user_id (user_id),
    INDEX idx_recurring_transactions_next_scheduled (next_scheduled_date),
    INDEX idx_recurring_transactions_active (is_active)
);

-- Create financial_goals table
CREATE TABLE financial_goals (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    goal_type VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    target_amount DECIMAL(19,2) NOT NULL,
    current_amount DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    target_date DATE NOT NULL,
    monthly_contribution DECIMAL(19,2),
    priority INT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_financial_goals_user_id (user_id),
    INDEX idx_financial_goals_target_date (target_date),
    INDEX idx_financial_goals_priority (priority)
);

-- Create budget_alerts table
CREATE TABLE budget_alerts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    budget_id BIGINT NOT NULL,
    alert_type VARCHAR(50) NOT NULL,
    threshold INT NOT NULL CHECK (threshold >= 0 AND threshold <= 100),
    message VARCHAR(255) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (budget_id) REFERENCES budgets(id) ON DELETE CASCADE,
    INDEX idx_budget_alerts_user_id (user_id),
    INDEX idx_budget_alerts_budget_id (budget_id),
    INDEX idx_budget_alerts_is_read (is_read)
);

-- Add composite indexes for performance
CREATE INDEX idx_categories_user_active ON categories(user_id, is_active);
CREATE INDEX idx_budgets_user_active ON budgets(user_id, is_active);
CREATE INDEX idx_recurring_user_active ON recurring_transactions(user_id, is_active);
CREATE INDEX idx_goals_user_active ON financial_goals(user_id, is_active);

-- Comments for documentation
COMMENT ON TABLE users IS 'User authentication and profile information';
COMMENT ON TABLE categories IS 'Income, expense, and savings categories for transactions';
COMMENT ON TABLE expenses IS 'Simple expense tracking (legacy table)';
COMMENT ON TABLE ledger_entries IS 'Main transaction ledger for all financial entries';
COMMENT ON TABLE budgets IS 'Budget definitions by period and category';
COMMENT ON TABLE recurring_transactions IS 'Scheduled recurring transactions';
COMMENT ON TABLE financial_goals IS 'User financial goals and savings targets';
COMMENT ON TABLE budget_alerts IS 'Budget threshold alerts and notifications';