-- ============================================================
-- Dayflow HRMS - Flyway Migration V4
-- Payroll Management Schema (Phase 7)
-- ============================================================

-- 1. Create PAYROLL Table
CREATE TABLE IF NOT EXISTS payroll (
    id UUID PRIMARY KEY,
    employee_id UUID NOT NULL UNIQUE,
    base_salary DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    allowances DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    deductions DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    net_salary DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payroll_employee FOREIGN KEY (employee_id) REFERENCES employees (id) ON DELETE CASCADE,
    CONSTRAINT chk_base_salary CHECK (base_salary >= 0),
    CONSTRAINT chk_allowances CHECK (allowances >= 0),
    CONSTRAINT chk_deductions CHECK (deductions >= 0)
);

-- 2. Create Index for employee lookups
CREATE INDEX IF NOT EXISTS idx_payroll_employee_id ON payroll (employee_id);
