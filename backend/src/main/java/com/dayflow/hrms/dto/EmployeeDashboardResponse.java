package com.dayflow.hrms.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for the Employee Dashboard API.
 * Aggregates employee profile, attendance, leave summary, recent notifications, and payroll.
 */
@Schema(description = "Aggregated employee dashboard data")
public class EmployeeDashboardResponse {

    @Schema(description = "Employee profile information")
    private EmployeeInfo employee;

    @Schema(description = "Today's attendance summary")
    private AttendanceInfo attendance;

    @Schema(description = "Leave request summary counts")
    private LeaveSummary leaveSummary;

    @Schema(description = "Recent notifications (latest 5)")
    private List<NotificationResponse> recentNotifications;

    @Schema(description = "Count of unread notifications")
    private long unreadNotificationsCount;

    @Schema(description = "Payroll compensation summary (null if no payroll record)")
    private PayrollSummary payroll;

    public EmployeeDashboardResponse() {
    }

    // Getters and Setters

    public EmployeeInfo getEmployee() { return employee; }
    public void setEmployee(EmployeeInfo employee) { this.employee = employee; }

    public AttendanceInfo getAttendance() { return attendance; }
    public void setAttendance(AttendanceInfo attendance) { this.attendance = attendance; }

    public LeaveSummary getLeaveSummary() { return leaveSummary; }
    public void setLeaveSummary(LeaveSummary leaveSummary) { this.leaveSummary = leaveSummary; }

    public List<NotificationResponse> getRecentNotifications() { return recentNotifications; }
    public void setRecentNotifications(List<NotificationResponse> recentNotifications) { this.recentNotifications = recentNotifications; }

    public long getUnreadNotificationsCount() { return unreadNotificationsCount; }
    public void setUnreadNotificationsCount(long unreadNotificationsCount) { this.unreadNotificationsCount = unreadNotificationsCount; }

    public PayrollSummary getPayroll() { return payroll; }
    public void setPayroll(PayrollSummary payroll) { this.payroll = payroll; }

    // ── Nested DTOs ──────────────────────────────────────────────

    @Schema(description = "Non-sensitive employee profile information")
    public static class EmployeeInfo {
        private UUID id;
        private String employeeCode;
        private String name;
        private String email;
        private String department;
        private String designation;
        private String employmentStatus;
        private LocalDate joiningDate;

        public EmployeeInfo() {}

        public EmployeeInfo(UUID id, String employeeCode, String name, String email,
                            String department, String designation, String employmentStatus,
                            LocalDate joiningDate) {
            this.id = id;
            this.employeeCode = employeeCode;
            this.name = name;
            this.email = email;
            this.department = department;
            this.designation = designation;
            this.employmentStatus = employmentStatus;
            this.joiningDate = joiningDate;
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getEmployeeCode() { return employeeCode; }
        public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getDesignation() { return designation; }
        public void setDesignation(String designation) { this.designation = designation; }
        public String getEmploymentStatus() { return employmentStatus; }
        public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }
        public LocalDate getJoiningDate() { return joiningDate; }
        public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }
    }

    @Schema(description = "Today's attendance status (placeholder until Attendance module is implemented)")
    public static class AttendanceInfo {
        @Schema(description = "Attendance status", example = "NOT_AVAILABLE")
        private String status;

        @Schema(description = "Check-in timestamp (null if not checked in)")
        private Instant checkInTime;

        @Schema(description = "Check-out timestamp (null if not checked out)")
        private Instant checkOutTime;

        @Schema(description = "Working hours for today (null if not available)")
        private Double workingHours;

        public AttendanceInfo() {}

        public AttendanceInfo(String status, Instant checkInTime, Instant checkOutTime, Double workingHours) {
            this.status = status;
            this.checkInTime = checkInTime;
            this.checkOutTime = checkOutTime;
            this.workingHours = workingHours;
        }

        /**
         * Factory for the "not available" placeholder state.
         */
        public static AttendanceInfo notAvailable() {
            return new AttendanceInfo("NOT_AVAILABLE", null, null, null);
        }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Instant getCheckInTime() { return checkInTime; }
        public void setCheckInTime(Instant checkInTime) { this.checkInTime = checkInTime; }
        public Instant getCheckOutTime() { return checkOutTime; }
        public void setCheckOutTime(Instant checkOutTime) { this.checkOutTime = checkOutTime; }
        public Double getWorkingHours() { return workingHours; }
        public void setWorkingHours(Double workingHours) { this.workingHours = workingHours; }
    }

    @Schema(description = "Leave request summary by status")
    public static class LeaveSummary {
        private long totalRequests;
        private long pending;
        private long approved;
        private long rejected;

        public LeaveSummary() {}

        public LeaveSummary(long totalRequests, long pending, long approved, long rejected) {
            this.totalRequests = totalRequests;
            this.pending = pending;
            this.approved = approved;
            this.rejected = rejected;
        }

        public long getTotalRequests() { return totalRequests; }
        public void setTotalRequests(long totalRequests) { this.totalRequests = totalRequests; }
        public long getPending() { return pending; }
        public void setPending(long pending) { this.pending = pending; }
        public long getApproved() { return approved; }
        public void setApproved(long approved) { this.approved = approved; }
        public long getRejected() { return rejected; }
        public void setRejected(long rejected) { this.rejected = rejected; }
    }

    @Schema(description = "Employee payroll compensation summary")
    public static class PayrollSummary {
        private BigDecimal baseSalary;
        private BigDecimal allowances;
        private BigDecimal deductions;
        private BigDecimal netSalary;

        public PayrollSummary() {}

        public PayrollSummary(BigDecimal baseSalary, BigDecimal allowances, BigDecimal deductions, BigDecimal netSalary) {
            this.baseSalary = baseSalary;
            this.allowances = allowances;
            this.deductions = deductions;
            this.netSalary = netSalary;
        }

        public BigDecimal getBaseSalary() { return baseSalary; }
        public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }
        public BigDecimal getAllowances() { return allowances; }
        public void setAllowances(BigDecimal allowances) { this.allowances = allowances; }
        public BigDecimal getDeductions() { return deductions; }
        public void setDeductions(BigDecimal deductions) { this.deductions = deductions; }
        public BigDecimal getNetSalary() { return netSalary; }
        public void setNetSalary(BigDecimal netSalary) { this.netSalary = netSalary; }
    }
}
