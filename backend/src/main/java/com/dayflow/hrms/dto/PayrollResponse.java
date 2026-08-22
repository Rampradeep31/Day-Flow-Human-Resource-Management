package com.dayflow.hrms.dto;

import com.dayflow.hrms.entity.Payroll;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO representing employee payroll compensation details.
 */
@Schema(description = "Response containing employee payroll and compensation details")
public class PayrollResponse {

    @Schema(description = "Payroll unique ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Employee ID", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID employeeId;

    @Schema(description = "Employee Code", example = "EMP101")
    private String employeeCode;

    @Schema(description = "Employee full name", example = "Dwight Schrute")
    private String employeeName;

    @Schema(description = "Department name", example = "Sales")
    private String department;

    @Schema(description = "Job designation", example = "Assistant Regional Manager")
    private String designation;

    @Schema(description = "Base salary", example = "50000.00")
    private BigDecimal baseSalary;

    @Schema(description = "Allowances", example = "5000.00")
    private BigDecimal allowances;

    @Schema(description = "Deductions", example = "2500.00")
    private BigDecimal deductions;

    @Schema(description = "Calculated net salary (Base + Allowances - Deductions)", example = "52500.00")
    private BigDecimal netSalary;

    @Schema(description = "Timestamp when payroll record was created")
    private Instant createdAt;

    @Schema(description = "Timestamp when payroll record was last updated")
    private Instant updatedAt;

    public PayrollResponse() {
    }

    public PayrollResponse(UUID id, UUID employeeId, String employeeCode, String employeeName,
                           String department, String designation, BigDecimal baseSalary,
                           BigDecimal allowances, BigDecimal deductions, BigDecimal netSalary,
                           Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.department = department;
        this.designation = designation;
        this.baseSalary = baseSalary;
        this.allowances = allowances;
        this.deductions = deductions;
        this.netSalary = netSalary;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PayrollResponse fromEntity(Payroll entity) {
        if (entity == null) {
            return null;
        }

        UUID empId = null;
        String empCode = null;
        String empName = null;
        String dept = null;
        String desig = null;

        if (entity.getEmployee() != null) {
            empId = entity.getEmployee().getId();
            empCode = entity.getEmployee().getEmployeeCode();
            empName = entity.getEmployee().getFullName();
            dept = entity.getEmployee().getDepartment();
            desig = entity.getEmployee().getDesignation();
        }

        return PayrollResponse.builder()
                .id(entity.getId())
                .employeeId(empId)
                .employeeCode(empCode)
                .employeeName(empName)
                .department(dept)
                .designation(desig)
                .baseSalary(entity.getBaseSalary())
                .allowances(entity.getAllowances())
                .deductions(entity.getDeductions())
                .netSalary(entity.getNetSalary())
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

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public BigDecimal getAllowances() {
        return allowances;
    }

    public void setAllowances(BigDecimal allowances) {
        this.allowances = allowances;
    }

    public BigDecimal getDeductions() {
        return deductions;
    }

    public void setDeductions(BigDecimal deductions) {
        this.deductions = deductions;
    }

    public BigDecimal getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary;
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
        private String designation;
        private BigDecimal baseSalary;
        private BigDecimal allowances;
        private BigDecimal deductions;
        private BigDecimal netSalary;
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

        public Builder designation(String designation) {
            this.designation = designation;
            return this;
        }

        public Builder baseSalary(BigDecimal baseSalary) {
            this.baseSalary = baseSalary;
            return this;
        }

        public Builder allowances(BigDecimal allowances) {
            this.allowances = allowances;
            return this;
        }

        public Builder deductions(BigDecimal deductions) {
            this.deductions = deductions;
            return this;
        }

        public Builder netSalary(BigDecimal netSalary) {
            this.netSalary = netSalary;
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

        public PayrollResponse build() {
            return new PayrollResponse(id, employeeId, employeeCode, employeeName, department,
                    designation, baseSalary, allowances, deductions, netSalary, createdAt, updatedAt);
        }
    }
}
