package com.dayflow.hrms.dto;

import com.dayflow.hrms.entity.LeaveType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO for employee leave submission.
 */
@Schema(description = "Payload for submitting a new leave request")
public class CreateLeaveRequest {

    @NotNull(message = "Leave type is required (PAID, SICK, UNPAID)")
    @Schema(description = "Type of leave requested", example = "PAID", requiredMode = Schema.RequiredMode.REQUIRED)
    private LeaveType leaveType;

    @NotNull(message = "Start date is required")
    @Schema(description = "Start date of leave (YYYY-MM-DD)", example = "2026-09-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Schema(description = "End date of leave (YYYY-MM-DD)", example = "2026-09-03", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate endDate;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    @Schema(description = "Optional remarks or reason for leave", example = "Family vacation")
    private String remarks;

    public CreateLeaveRequest() {
    }

    public CreateLeaveRequest(LeaveType leaveType, LocalDate startDate, LocalDate endDate, String remarks) {
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remarks = remarks;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LeaveType leaveType;
        private LocalDate startDate;
        private LocalDate endDate;
        private String remarks;

        public Builder leaveType(LeaveType leaveType) {
            this.leaveType = leaveType;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder remarks(String remarks) {
            this.remarks = remarks;
            return this;
        }

        public CreateLeaveRequest build() {
            return new CreateLeaveRequest(leaveType, startDate, endDate, remarks);
        }
    }
}
