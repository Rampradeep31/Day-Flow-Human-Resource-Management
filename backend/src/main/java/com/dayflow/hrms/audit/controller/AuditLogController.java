package com.dayflow.hrms.audit.controller;

import com.dayflow.hrms.audit.dto.AuditLogResponse;
import com.dayflow.hrms.audit.enums.AuditAction;
import com.dayflow.hrms.audit.enums.AuditResourceType;
import com.dayflow.hrms.audit.enums.AuditStatus;
import com.dayflow.hrms.audit.service.AuditLogService;
import com.dayflow.hrms.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-logs")
@Tag(name = "Audit Logs", description = "Append-only activity history restricted to HR and ADMIN")
@SecurityRequirement(name = "bearerAuth")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @Operation(summary = "Query audit logs", description = "Returns newest-first audit events with validated filters and bounded pagination.")
    @ApiResponse(responseCode = "200", description = "Audit events returned")
    @ApiResponse(responseCode = "400", description = "Invalid filter, date range, pagination, or sort")
    @ApiResponse(responseCode = "401", description = "Unauthenticated")
    @ApiResponse(responseCode = "403", description = "EMPLOYEE role is not permitted")
    public ResponseEntity<PageResponse<AuditLogResponse>> getAuditLogs(
            @RequestParam(required = false) UUID actorUserId,
            @RequestParam(required = false) UUID actorEmployeeId,
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) AuditResourceType resourceType,
            @RequestParam(required = false) UUID resourceId,
            @RequestParam(required = false) AuditStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(auditLogService.getAuditLogs(actorUserId, actorEmployeeId, action,
                resourceType, resourceId, status, from, to, page, size, sort, direction));
    }
}
