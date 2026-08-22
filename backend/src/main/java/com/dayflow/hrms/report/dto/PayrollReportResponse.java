package com.dayflow.hrms.report.dto;

import com.dayflow.hrms.entity.Payroll;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Sensitive payroll report row, restricted to HR and ADMIN")
public record PayrollReportResponse(
        UUID payrollId,
        UUID employeeId,
        String employeeCode,
        String employeeName,
        String department,
        String designation,
        BigDecimal baseSalary,
        BigDecimal allowances,
        BigDecimal deductions,
        BigDecimal netSalary,
        Instant updatedAt) {

    public static PayrollReportResponse fromEntity(Payroll payroll) {
        return new PayrollReportResponse(payroll.getId(), payroll.getEmployee().getId(),
                payroll.getEmployee().getEmployeeCode(), payroll.getEmployee().getFullName(),
                payroll.getEmployee().getDepartment(), payroll.getEmployee().getDesignation(),
                payroll.getBaseSalary(), payroll.getAllowances(), payroll.getDeductions(),
                payroll.getNetSalary(), payroll.getUpdatedAt());
    }
}
