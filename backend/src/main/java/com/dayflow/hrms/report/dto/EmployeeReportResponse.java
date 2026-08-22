package com.dayflow.hrms.report.dto;

import com.dayflow.hrms.entity.Employee;
import com.dayflow.hrms.entity.EmploymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Non-sensitive employee report row")
public record EmployeeReportResponse(
        UUID employeeId,
        String employeeCode,
        String name,
        String department,
        String designation,
        EmploymentStatus employmentStatus,
        LocalDate joiningDate) {

    public static EmployeeReportResponse fromEntity(Employee employee) {
        return new EmployeeReportResponse(employee.getId(), employee.getEmployeeCode(), employee.getFullName(),
                employee.getDepartment(), employee.getDesignation(), employee.getEmploymentStatus(),
                employee.getJoiningDate());
    }
}
