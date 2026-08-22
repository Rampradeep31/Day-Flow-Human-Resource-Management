package com.dayflow.hrms.dto;

import com.dayflow.hrms.entity.LeaveRequest;
import com.dayflow.hrms.entity.LeaveStatus;
import com.dayflow.hrms.entity.LeaveType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response DTO representing leave request details.
 */
@Schema(description = "Response containing leave request details")
public class LeaveResponse {

    @Schema(description = "Leave request unique ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Employee ID", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID employeeId;

    @Schema(description = "Employee Code", example = "EMP101")
    private String employeeCode;

    @Schema(description = "Employee full name", example = "Dwight Schrute")
    private String employeeName;

    @Schema(description = "Department name", example = "Sales")
    private String department;

    @Schema(description = "Leave type", example = "PAID")
    private LeaveType leaveType;

    @Schema(description = "Leave start date", example = "2026-09-01")
    private LocalDate startDate;

    @Schema(description = "Leave end date", example = "2026-09-03")
    private LocalDate endDate;

    @Schema(description = "Total number of leave days (inclusive)", example = "3")
    private long durationDays;

    @Schema(description = "Employee remarks", example = "Family vacation")
    private String remarks;

    @Schema(description = "Leave request status", example = "PENDING")
    private LeaveStatus status;

    @Schema(description = "HR/Admin reviewer comments", example = "Approved based on available quota")
    private String reviewComment;

    @Schema(description = "User ID of the reviewer", example = "123e4567-e89b-12d3-a456-426614174002")
    private UUID reviewedByUserId;

    @Schema(description = "Email of the reviewer", example = "hr.manager@dayflow.com")
    private String reviewedByEmail;

    @Schema(description = "Timestamp when leave was reviewed")
    private Instant reviewedAt;

    @Schema(description = "Timestamp when request was created")
    private Instant createdAt;

    @Schema(description = "Timestamp when request was last updated")
    private Instant updatedAt;

    public LeaveResponse() {
    }

    public LeaveResponse(UUID id, UUID employeeId, String employeeCode, String employeeName,
                         String department, LeaveType leaveType, LocalDate startDate,
                         LocalDate endDate, long durationDays, String remarks,
                         LeaveStatus status, String reviewComment, UUID reviewedByUserId,
                         String reviewedByEmail, Instant reviewedAt, Instant createdAt,
                         Instant updatedAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.department = department;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.durationDays = durationDays;
        this.remarks = remarks;
        this.status = status;
        this.reviewComment = reviewComment;
        this.reviewedByUserId = reviewedByUserId;
        this.reviewedByEmail = reviewedByEmail;
        this.reviewedAt = reviewedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static LeaveResponse fromEntity(LeaveRequest entity) {
        if (entity == null) {
            return null;
        }

        UUID empId = null;
        String empCode = null;
        String empName = null;
        String dept = null;

        if (entity.getEmployee() != null) {
            empId = entity.getEmployee().getId();
            empCode = entity.getEmployee().getEmployeeCode();
            empName = entity.getEmployee().getFullName();
            dept = entity.getEmployee().getDepartment();
        }

        UUID revUserId = null;
        String revEmail = null;
        if (entity.getReviewedBy() != null) {
            revUserId = entity.getReviewedBy().getId();
            revEmail = entity.getReviewedBy().getEmail();
        }

        return LeaveResponse.builder()
                .id(entity.getId())
                .employeeId(empId)
                .employeeCode(empCode)
                .employeeName(empName)
                .department(dept)
                .leaveType(entity.getLeaveType())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .durationDays(entity.getDurationInDays())
                .remarks(entity.getRemarks())
                .status(entity.getStatus())
                .reviewComment(entity.getReviewComment())
                .reviewedByUserId(revUserId)
                .reviewedByEmail(revEmail)
                .reviewedAt(entity.getReviewedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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

    public long getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(long durationDays) {
        this.durationDays = durationDays;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public UUID getReviewedByUserId() {
        return reviewedByUserId;
    }

    public void setReviewedByUserId(UUID reviewedByUserId) {
        this.reviewedByUserId = reviewedByUserId;
    }

    public String getReviewedByEmail() {
        return reviewedByEmail;
    }

    public void setReviewedByEmail(String reviewedByEmail) {
        this.reviewedByEmail = reviewedByEmail;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Instant reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID employeeId;
        private String employeeCode;
        private String employeeName;
        private String department;
        private LeaveType leaveType;
        private LocalDate startDate;
        private LocalDate endDate;
        private long durationDays;
        private String remarks;
        private LeaveStatus status;
        private String reviewComment;
        private UUID reviewedByUserId;
        private String reviewedByEmail;
        private Instant reviewedAt;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder employeeId(UUID employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public Builder employeeCode(String employeeCode) {
            this.employeeCode = employeeCode;
            return this;
        }

        public Builder employeeName(String employeeName) {
            this.employeeName = employeeName;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

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

        public Builder durationDays(long durationDays) {
            this.durationDays = durationDays;
            return this;
        }

        public Builder remarks(String remarks) {
            this.remarks = remarks;
            return this;
        }

        public Builder status(LeaveStatus status) {
            this.status = status;
            return this;
        }

        public Builder reviewComment(String reviewComment) {
            this.reviewComment = reviewComment;
            return this;
        }

        public Builder reviewedByUserId(UUID reviewedByUserId) {
            this.reviewedByUserId = reviewedByUserId;
            return this;
        }

        public Builder reviewedByEmail(String reviewedByEmail) {
            this.reviewedByEmail = reviewedByEmail;
            return this;
        }

        public Builder reviewedAt(Instant reviewedAt) {
            this.reviewedAt = reviewedAt;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public LeaveResponse build() {
            return new LeaveResponse(id, employeeId, employeeCode, employeeName, department,
                    leaveType, startDate, endDate, durationDays, remarks, status,
                    reviewComment, reviewedByUserId, reviewedByEmail, reviewedAt, createdAt, updatedAt);
        }
    }
}
