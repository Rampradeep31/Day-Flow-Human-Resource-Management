package com.dayflow.hrms.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Department-level organization report")
public record DepartmentReportResponse(
        String department,
        long employeeCount,
        long activeEmployeeCount,
        long leaveRequestCount,
        long approvedLeaveRequestCount,
        BigDecimal totalBaseSalary,
        BigDecimal totalAllowances,
        BigDecimal totalDeductions,
        BigDecimal totalNetSalary) {
}
