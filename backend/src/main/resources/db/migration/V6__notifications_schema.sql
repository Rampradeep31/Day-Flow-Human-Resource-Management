-- ============================================================
-- Dayflow HRMS - Flyway Migration V6
-- Notifications Management Schema (Phase 9)
-- ============================================================

-- 1. Create NOTIFICATIONS Table
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY,
    employee_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP WITH TIME ZONE,
    reference_type VARCHAR(50),
    reference_id UUID,
    CONSTRAINT fk_notifications_employee FOREIGN KEY (employee_id) REFERENCES employees (id) ON DELETE CASCADE
);

-- 2. Create Indexes for query performance
CREATE INDEX IF NOT EXISTS idx_notifications_employee_id ON notifications (employee_id);
CREATE INDEX IF NOT EXISTS idx_notifications_employee_unread ON notifications (employee_id, is_read);
CREATE INDEX IF NOT EXISTS idx_notifications_employee_created ON notifications (employee_id, created_at);
