-- ============================================================
-- Dayflow HRMS - Flyway Migration V3
-- Leave & Time-Off Management Schema (Phase 6)
-- ============================================================

-- 1. Create LEAVE_REQUESTS Table
CREATE TABLE IF NOT EXISTS leave_requests (
    id UUID PRIMARY KEY,
    employee_id UUID NOT NULL,
    leave_type VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    remarks VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    review_comment VARCHAR(500),
    reviewed_by UUID,
    reviewed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_leave_requests_employee FOREIGN KEY (employee_id) REFERENCES employees (id) ON DELETE CASCADE,
    CONSTRAINT fk_leave_requests_reviewer FOREIGN KEY (reviewed_by) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT chk_leave_dates CHECK (start_date <= end_date)
);

-- 2. Create Indexes for performance and frequent queries
CREATE INDEX IF NOT EXISTS idx_leave_requests_employee_id ON leave_requests (employee_id);
CREATE INDEX IF NOT EXISTS idx_leave_requests_status ON leave_requests (status);
CREATE INDEX IF NOT EXISTS idx_leave_requests_start_date ON leave_requests (start_date);
CREATE INDEX IF NOT EXISTS idx_leave_requests_end_date ON leave_requests (end_date);
CREATE INDEX IF NOT EXISTS idx_leave_requests_emp_status ON leave_requests (employee_id, status);
CREATE INDEX IF NOT EXISTS idx_leave_requests_date_range ON leave_requests (start_date, end_date);
