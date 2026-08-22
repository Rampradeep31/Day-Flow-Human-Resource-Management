package com.dayflow.hrms.controller;

import com.dayflow.hrms.dto.PayrollResponse;
import com.dayflow.hrms.dto.UpdatePayrollRequest;
import com.dayflow.hrms.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for Payroll Management and compensation retrieval.
 */
@RestController
@RequestMapping("/api/v1/payroll")
@Tag(name = "Payroll", description = "Payroll and salary compensation endpoints")
@SecurityRequirement(name = "bearerAuth")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'ADMIN')")
    @Operation(summary = "Get own payroll details", description = "Retrieves payroll compensation details of the authenticated employee")
    @ApiResponse(responseCode = "200", description = "Payroll details retrieved")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Payroll record not found")
    public ResponseEntity<PayrollResponse> getMyPayroll() {
        return ResponseEntity.ok(payrollService.getMyPayroll());
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'ADMIN')")
    @Operation(summary = "Get employee payroll by ID", description = "Retrieves payroll of a specific employee. Non-HR/Admin users can only view their own payroll.")
    @ApiResponse(responseCode = "200", description = "Payroll details retrieved")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - Cannot view another employee's payroll")
    @ApiResponse(responseCode = "404", description = "Employee or payroll not found")
    public ResponseEntity<PayrollResponse> getEmployeePayroll(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(payrollService.getEmployeePayroll(employeeId));
    }

    @PutMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @Operation(summary = "Update employee payroll", description = "Updates or initializes base salary, allowances, and deductions for an employee")
    @ApiResponse(responseCode = "200", description = "Payroll updated successfully")
    @ApiResponse(responseCode = "400", description = "Bad request - Invalid salary input")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - Requires HR or ADMIN role")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    public ResponseEntity<PayrollResponse> updateEmployeePayroll(
            @PathVariable UUID employeeId,
            @Valid @RequestBody UpdatePayrollRequest request) {

        return ResponseEntity.ok(payrollService.updateEmployeePayroll(employeeId, request));
    }
}
