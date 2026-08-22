package com.dayflow.hrms.report.controller;

import com.dayflow.hrms.dto.PageResponse;
import com.dayflow.hrms.entity.EmploymentStatus;
import com.dayflow.hrms.entity.LeaveStatus;
import com.dayflow.hrms.entity.LeaveType;
import com.dayflow.hrms.report.dto.*;
import com.dayflow.hrms.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports", description = "Read-only, filtered HRMS reports")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/employees")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'ADMIN')")
    @Operation(summary = "Employee report", description = "HR/ADMIN receive organization data; EMPLOYEE is securely limited to their own row.")
    @ApiResponse(responseCode = "200", description = "Report returned")
    @ApiResponse(responseCode = "400", description = "Invalid filter, date range, pagination, or sort")
    @ApiResponse(responseCode = "401", description = "Unauthenticated")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    public ResponseEntity<PageResponse<EmployeeReportResponse>> employees(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) EmploymentStatus employmentStatus,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "employeeName") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(reportService.getEmployeeReport(department, employmentStatus, designation,
                from, to, page, size, sort, direction));
    }

    @GetMapping("/attendance")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'ADMIN')")
    @Operation(summary = "Attendance report", description = "Ownership-protected attendance report. This schema currently contains no attendance persistence, so the result is an empty page.")
    public ResponseEntity<PageResponse<AttendanceReportResponse>> attendance(
            @RequestParam(required = false) UUID employeeId,
            @Parameter(description = "Inclusive ISO date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Inclusive ISO date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "date") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(reportService.getAttendanceReport(employeeId, from, to, page, size, sort, direction));
    }

    @GetMapping("/leaves")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'ADMIN')")
    @Operation(summary = "Leave report", description = "Filtered leave report. EMPLOYEE is securely limited to their own leave data.")
    public ResponseEntity<PageResponse<LeaveReportResponse>> leaves(
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) LeaveType leaveType,
            @RequestParam(required = false) LeaveStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(reportService.getLeaveReport(employeeId, leaveType, status, from, to,
                department, page, size, sort, direction));
    }

    @GetMapping("/payroll")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @Operation(summary = "Payroll report", description = "Sensitive organization payroll report restricted to HR and ADMIN.")
    @ApiResponse(responseCode = "403", description = "EMPLOYEE role is not permitted")
    public ResponseEntity<PageResponse<PayrollReportResponse>> payroll(
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) EmploymentStatus employmentStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "updatedAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(reportService.getPayrollReport(employeeId, department, employmentStatus,
                from, to, page, size, sort, direction));
    }

    @GetMapping("/departments")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @Operation(summary = "Department report", description = "Database-aggregated employee, leave, and payroll statistics by department.")
    @ApiResponse(responseCode = "403", description = "EMPLOYEE role is not permitted")
    public ResponseEntity<List<DepartmentReportResponse>> departments() {
        return ResponseEntity.ok(reportService.getDepartmentReport());
    }
}
