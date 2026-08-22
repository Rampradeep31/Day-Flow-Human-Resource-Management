package com.dayflow.hrms.controller;

import com.dayflow.hrms.dto.*;
import com.dayflow.hrms.entity.EmploymentStatus;
import com.dayflow.hrms.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for Employee administrative operations and directory access.
 */
@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Employees", description = "Employee management and directory endpoints")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @Operation(summary = "Get employees directory", description = "Retrieves paginated and filtered list of employees")
    @ApiResponse(responseCode = "200", description = "Employee list retrieved")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - Requires HR or ADMIN role")
    public ResponseEntity<PageResponse<EmployeeResponse>> getEmployees(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) EmploymentStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(employeeService.getEmployees(search, department, status, pageable));
    }

    @GetMapping("/{employeeId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'ADMIN')")
    @Operation(summary = "Get employee by ID", description = "Retrieves employee details. Non-HR/Admin users can only view their own record.")
    @ApiResponse(responseCode = "200", description = "Employee retrieved")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(employeeService.getEmployeeById(employeeId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @Operation(summary = "Create employee", description = "Creates a new employee record and binds it to a user account")
    @ApiResponse(responseCode = "201", description = "Employee created")
    @ApiResponse(responseCode = "400", description = "Bad request - validation or format error")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - Requires HR or ADMIN role")
    @ApiResponse(responseCode = "409", description = "Conflict - Employee code or user already exists")
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{employeeId}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @Operation(summary = "Update employee", description = "Updates an existing employee record")
    @ApiResponse(responseCode = "200", description = "Employee updated")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - Requires HR or ADMIN role")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable UUID employeeId,
            @Valid @RequestBody UpdateEmployeeRequest request) {

        return ResponseEntity.ok(employeeService.updateEmployee(employeeId, request));
    }

    @PatchMapping("/{employeeId}/status")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @Operation(summary = "Change employee status", description = "Changes employment status of an employee and updates associated user account status")
    @ApiResponse(responseCode = "200", description = "Employee status updated")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - Requires HR or ADMIN role")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    public ResponseEntity<EmployeeResponse> changeEmployeeStatus(
            @PathVariable UUID employeeId,
            @Valid @RequestBody ChangeEmployeeStatusRequest request) {

        return ResponseEntity.ok(employeeService.changeEmployeeStatus(employeeId, request));
    }
}
