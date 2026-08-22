package com.dayflow.hrms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Payload for updating or initializing employee payroll salary details.
 */
@Schema(description = "Payload for updating employee payroll salary structure")
public class UpdatePayrollRequest {

    @NotNull(message = "Base salary is required")
    @DecimalMin(value = "0.00", message = "Base salary cannot be negative")
    @Digits(integer = 13, fraction = 2, message = "Base salary must have at most 13 digits and 2 decimal places")
    @Schema(description = "Base salary amount", example = "50000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal baseSalary;

    @DecimalMin(value = "0.00", message = "Allowances cannot be negative")
    @Digits(integer = 13, fraction = 2, message = "Allowances must have at most 13 digits and 2 decimal places")
    @Schema(description = "Allowances amount", example = "5000.00")
    private BigDecimal allowances;

    @DecimalMin(value = "0.00", message = "Deductions cannot be negative")
    @Digits(integer = 13, fraction = 2, message = "Deductions must have at most 13 digits and 2 decimal places")
    @Schema(description = "Deductions amount", example = "2500.00")
    private BigDecimal deductions;

    public UpdatePayrollRequest() {
    }

    public UpdatePayrollRequest(BigDecimal baseSalary, BigDecimal allowances, BigDecimal deductions) {
        this.baseSalary = baseSalary;
        this.allowances = allowances;
        this.deductions = deductions;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BigDecimal baseSalary;
        private BigDecimal allowances;
        private BigDecimal deductions;

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

        public UpdatePayrollRequest build() {
            return new UpdatePayrollRequest(baseSalary, allowances, deductions);
        }
    }
}
