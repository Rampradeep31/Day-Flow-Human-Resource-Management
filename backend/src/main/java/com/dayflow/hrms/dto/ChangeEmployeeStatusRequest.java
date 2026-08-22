package com.dayflow.hrms.dto;

import com.dayflow.hrms.entity.EmploymentStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for HR/Admin changing an employee's employment status.
 */
public class ChangeEmployeeStatusRequest {

    @NotNull(message = "Employment status is required")
    private EmploymentStatus employmentStatus;

    public ChangeEmployeeStatusRequest() {
    }

    public ChangeEmployeeStatusRequest(EmploymentStatus employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(EmploymentStatus employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private EmploymentStatus employmentStatus;

        public Builder employmentStatus(EmploymentStatus employmentStatus) {
            this.employmentStatus = employmentStatus;
            return this;
        }

        public ChangeEmployeeStatusRequest build() {
            return new ChangeEmployeeStatusRequest(employmentStatus);
        }
    }
}
