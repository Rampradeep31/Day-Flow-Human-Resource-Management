package com.dayflow.hrms.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Response DTO for the Admin Dashboard API.
 * Aggregates organization-level employee, attendance, leave, department, and payroll statistics.
 */
@Schema(description = "Aggregated admin/HR dashboard data")
public class AdminDashboardResponse {

    @Schema(description = "Organization employee statistics")
    private EmployeeStatistics employeeStatistics;

    @Schema(description = "Today's attendance statistics (placeholder until Attendance module)")
    private AttendanceStatistics attendanceStatistics;

    @Schema(description = "Organization-wide leave statistics")
    private LeaveStatistics leaveStatistics;

    @Schema(description = "Employee distribution by department")
    private List<DepartmentStat> departmentStatistics;

    @Schema(description = "Recent organization activities")
    private List<RecentActivity> recentActivities;

    @Schema(description = "Aggregate payroll summary")
    private PayrollSummary payrollSummary;

    public AdminDashboardResponse() {
    }

    // Getters and Setters

    public EmployeeStatistics getEmployeeStatistics() { return employeeStatistics; }
    public void setEmployeeStatistics(EmployeeStatistics employeeStatistics) { this.employeeStatistics = employeeStatistics; }

    public AttendanceStatistics getAttendanceStatistics() { return attendanceStatistics; }
    public void setAttendanceStatistics(AttendanceStatistics attendanceStatistics) { this.attendanceStatistics = attendanceStatistics; }

    public LeaveStatistics getLeaveStatistics() { return leaveStatistics; }
    public void setLeaveStatistics(LeaveStatistics leaveStatistics) { this.leaveStatistics = leaveStatistics; }

    public List<DepartmentStat> getDepartmentStatistics() { return departmentStatistics; }
    public void setDepartmentStatistics(List<DepartmentStat> departmentStatistics) { this.departmentStatistics = departmentStatistics; }

    public List<RecentActivity> getRecentActivities() { return recentActivities; }
    public void setRecentActivities(List<RecentActivity> recentActivities) { this.recentActivities = recentActivities; }

    public PayrollSummary getPayrollSummary() { return payrollSummary; }
    public void setPayrollSummary(PayrollSummary payrollSummary) { this.payrollSummary = payrollSummary; }

    // ── Nested DTOs ──────────────────────────────────────────────

    @Schema(description = "Employee count statistics")
    public static class EmployeeStatistics {
        private long total;
        private long active;

        public EmployeeStatistics() {}

        public EmployeeStatistics(long total, long active) {
            this.total = total;
            this.active = active;
        }

        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }
        public long getActive() { return active; }
        public void setActive(long active) { this.active = active; }
    }

    @Schema(description = "Today's attendance statistics (placeholder until Attendance module)")
    public static class AttendanceStatistics {
        @Schema(description = "Employees present today (0 — Attendance module not yet implemented)")
        private long presentToday;

        @Schema(description = "Employees absent today (0 — Attendance module not yet implemented)")
        private long absentToday;

        public AttendanceStatistics() {}

        public AttendanceStatistics(long presentToday, long absentToday) {
            this.presentToday = presentToday;
            this.absentToday = absentToday;
        }

        /**
         * Factory for a placeholder until the Attendance module is implemented.
         */
        public static AttendanceStatistics notAvailable() {
            return new AttendanceStatistics(0, 0);
        }

        public long getPresentToday() { return presentToday; }
        public void setPresentToday(long presentToday) { this.presentToday = presentToday; }
        public long getAbsentToday() { return absentToday; }
        public void setAbsentToday(long absentToday) { this.absentToday = absentToday; }
    }

    @Schema(description = "Organization-wide leave statistics")
    public static class LeaveStatistics {
        private long onLeaveToday;
        private long pendingRequests;

        public LeaveStatistics() {}

        public LeaveStatistics(long onLeaveToday, long pendingRequests) {
            this.onLeaveToday = onLeaveToday;
            this.pendingRequests = pendingRequests;
        }

        public long getOnLeaveToday() { return onLeaveToday; }
        public void setOnLeaveToday(long onLeaveToday) { this.onLeaveToday = onLeaveToday; }
        public long getPendingRequests() { return pendingRequests; }
        public void setPendingRequests(long pendingRequests) { this.pendingRequests = pendingRequests; }
    }

    @Schema(description = "Employee count per department")
    public static class DepartmentStat {
        private String department;
        private long employeeCount;

        public DepartmentStat() {}

        public DepartmentStat(String department, long employeeCount) {
            this.department = department;
            this.employeeCount = employeeCount;
        }

        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public long getEmployeeCount() { return employeeCount; }
        public void setEmployeeCount(long employeeCount) { this.employeeCount = employeeCount; }
    }

    @Schema(description = "Recent organization activity entry")
    public static class RecentActivity {
        private String type;
        private String description;
        private Instant timestamp;

        public RecentActivity() {}

        public RecentActivity(String type, String description, Instant timestamp) {
            this.type = type;
            this.description = description;
            this.timestamp = timestamp;
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Instant getTimestamp() { return timestamp; }
        public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    }

    @Schema(description = "Aggregate payroll cost summary")
    public static class PayrollSummary {
        private BigDecimal totalBaseSalary;
        private BigDecimal totalAllowances;
        private BigDecimal totalDeductions;
        private BigDecimal totalNetSalary;

        public PayrollSummary() {}

        public PayrollSummary(BigDecimal totalBaseSalary, BigDecimal totalAllowances,
                              BigDecimal totalDeductions, BigDecimal totalNetSalary) {
            this.totalBaseSalary = totalBaseSalary;
            this.totalAllowances = totalAllowances;
            this.totalDeductions = totalDeductions;
            this.totalNetSalary = totalNetSalary;
        }

        public BigDecimal getTotalBaseSalary() { return totalBaseSalary; }
        public void setTotalBaseSalary(BigDecimal totalBaseSalary) { this.totalBaseSalary = totalBaseSalary; }
        public BigDecimal getTotalAllowances() { return totalAllowances; }
        public void setTotalAllowances(BigDecimal totalAllowances) { this.totalAllowances = totalAllowances; }
        public BigDecimal getTotalDeductions() { return totalDeductions; }
        public void setTotalDeductions(BigDecimal totalDeductions) { this.totalDeductions = totalDeductions; }
        public BigDecimal getTotalNetSalary() { return totalNetSalary; }
        public void setTotalNetSalary(BigDecimal totalNetSalary) { this.totalNetSalary = totalNetSalary; }
    }
}
