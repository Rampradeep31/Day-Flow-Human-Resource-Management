package com.dayflow.hrms.controller;

import com.dayflow.hrms.dto.AdminDashboardResponse;
import com.dayflow.hrms.dto.EmployeeDashboardResponse;
import com.dayflow.hrms.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Dashboard APIs.
 * Read-only aggregation endpoints — no data modification.
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Aggregated dashboard data for employees and administrators")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/employee")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'ADMIN')")
    @Operation(summary = "Get employee dashboard",
            description = "Retrieves aggregated dashboard data for the authenticated employee: profile, attendance, leave summary, recent notifications, and payroll.")
    @ApiResponse(responseCode = "200", description = "Dashboard data retrieved")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Employee profile not found")
    public ResponseEntity<EmployeeDashboardResponse> getEmployeeDashboard() {
        return ResponseEntity.ok(dashboardService.getEmployeeDashboard());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @Operation(summary = "Get admin dashboard",
            description = "Retrieves organization-level dashboard data: employee statistics, attendance statistics, leave statistics, department distribution, recent activities, and payroll summary.")
    @ApiResponse(responseCode = "200", description = "Dashboard data retrieved")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden — EMPLOYEE role not allowed")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }
}
