-- ============================================================
-- Dayflow HRMS - Flyway Migration V5
-- Document Management Schema (Phase 8)
-- ============================================================

-- 1. Create DOCUMENTS Table
CREATE TABLE IF NOT EXISTS documents (
    id UUID PRIMARY KEY,
    employee_id UUID NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    storage_path VARCHAR(500) NOT NULL UNIQUE,
    content_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_documents_employee FOREIGN KEY (employee_id) REFERENCES employees (id) ON DELETE CASCADE
);

-- 2. Create Indexes for query performance
CREATE INDEX IF NOT EXISTS idx_documents_employee_id ON documents (employee_id);
CREATE INDEX IF NOT EXISTS idx_documents_created_at ON documents (created_at);
CREATE INDEX IF NOT EXISTS idx_documents_employee_created ON documents (employee_id, created_at);
