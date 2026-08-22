package com.dayflow.hrms.controller;

import com.dayflow.hrms.dto.*;
import com.dayflow.hrms.entity.LeaveStatus;
import com.dayflow.hrms.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * REST Controller for Leave & Time-Off Management.
 */
@RestController
@RequestMapping("/api/v1/leaves")
@Tag(name = "Leaves", description = "Leave application, approval, and management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'ADMIN')")
    @Operation(summary = "Apply for leave", description = "Authenticated employee submits a new leave request. Status is initialized as PENDING.")
    @ApiResponse(responseCode = "201", description = "Leave request created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request payload or date range")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "409", description = "Conflict - Overlaps with an existing active leave request")
    public ResponseEntity<LeaveResponse> applyLeave(@Valid @RequestBody CreateLeaveRequest request) {
        LeaveResponse response = leaveService.applyLeave(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'ADMIN')")
    @Operation(summary = "Get own leave history", description = "Retrieves paginated leave history of the currently authenticated employee")
    @ApiResponse(responseCode = "200", description = "Leave history retrieved")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<PageResponse<LeaveResponse>> getOwnLeaves(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(leaveService.getOwnLeaves(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'ADMIN')")
    @Operation(summary = "Get leave request by ID", description = "Retrieves a leave request. Regular employees can only view their own requests.")
    @ApiResponse(responseCode = "200", description = "Leave request retrieved")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - Cannot view another employee's leave")
    @ApiResponse(responseCode = "404", description = "Leave request not found")
    public ResponseEntity<LeaveResponse> getLeaveById(@PathVariable UUID id) {
        return ResponseEntity.ok(leaveService.getLeaveById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @Operation(summary = "Get all leave requests", description = "Retrieves paginated leave requests with optional filters for HR/Admin")
    @ApiResponse(responseCode = "200", description = "Leave requests retrieved")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - Requires HR or ADMIN role")
    public ResponseEntity<PageResponse<LeaveResponse>> getLeaves(
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) LeaveStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(leaveService.getLeaves(employeeId, status, startDate, endDate, pageable));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @Operation(summary = "Get employee leaves", description = "Retrieves all leave requests of a specific employee for HR/Admin")
    @ApiResponse(responseCode = "200", description = "Employee leave requests retrieved")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - Requires HR or ADMIN role")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    public ResponseEntity<PageResponse<LeaveResponse>> getEmployeeLeaves(
            @PathVariable UUID employeeId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(leaveService.getEmployeeLeaves(employeeId, pageable));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @Operation(summary = "Approve leave request", description = "HR/Admin approves a pending leave request with optional comments")
    @ApiResponse(responseCode = "200", description = "Leave request approved")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - Requires HR or ADMIN role")
    @ApiResponse(responseCode = "404", description = "Leave request not found")
    @ApiResponse(responseCode = "409", description = "Conflict - Leave request is already approved or rejected")
    public ResponseEntity<LeaveResponse> approveLeave(
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) ReviewLeaveRequest request) {

        return ResponseEntity.ok(leaveService.approveLeave(id, request));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @Operation(summary = "Reject leave request", description = "HR/Admin rejects a pending leave request with optional/required comments")
    @ApiResponse(responseCode = "200", description = "Leave request rejected")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - Requires HR or ADMIN role")
    @ApiResponse(responseCode = "404", description = "Leave request not found")
    @ApiResponse(responseCode = "409", description = "Conflict - Leave request is already approved or rejected")
    public ResponseEntity<LeaveResponse> rejectLeave(
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) ReviewLeaveRequest request) {

        return ResponseEntity.ok(leaveService.rejectLeave(id, request));
    }
}
