-- ============================================================
-- Dayflow HRMS - Flyway Migration V2
-- Employee Management Schema
-- ============================================================

-- 1. Create employees table
CREATE TABLE IF NOT EXISTS employees (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    employee_code VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(30),
    address TEXT,
    date_of_birth DATE,
    gender VARCHAR(20),
    profile_picture_url VARCHAR(500),
    department VARCHAR(100),
    designation VARCHAR(100),
    joining_date DATE NOT NULL,
    employment_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_employees_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- 2. Create indexes for high-frequency queries
CREATE INDEX IF NOT EXISTS idx_employees_user_id ON employees (user_id);
CREATE INDEX IF NOT EXISTS idx_employees_employee_code ON employees (employee_code);
CREATE INDEX IF NOT EXISTS idx_employees_department ON employees (department);
CREATE INDEX IF NOT EXISTS idx_employees_employment_status ON employees (employment_status);
CREATE INDEX IF NOT EXISTS idx_employees_first_last_name ON employees (first_name, last_name);
