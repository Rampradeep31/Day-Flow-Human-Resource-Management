package com.dayflow.hrms.audit.service;

import com.dayflow.hrms.audit.dto.AuditLogResponse;
import com.dayflow.hrms.audit.enums.AuditAction;
import com.dayflow.hrms.audit.enums.AuditResourceType;
import com.dayflow.hrms.audit.enums.AuditStatus;
import com.dayflow.hrms.dto.PageResponse;

import java.time.LocalDate;
import java.util.UUID;

public interface AuditLogService {
    void log(AuditAction action, AuditResourceType resourceType, UUID resourceId,
             String description, AuditStatus status);

    default void logSuccess(AuditAction action, AuditResourceType resourceType,
                            UUID resourceId, String description) {
        log(action, resourceType, resourceId, description, AuditStatus.SUCCESS);
    }

    default void logFailure(AuditAction action, AuditResourceType resourceType,
                            UUID resourceId, String description) {
        log(action, resourceType, resourceId, description, AuditStatus.FAILURE);
    }

    default void logDenied(AuditAction action, AuditResourceType resourceType,
                           UUID resourceId, String description) {
        log(action, resourceType, resourceId, description, AuditStatus.DENIED);
    }

    PageResponse<AuditLogResponse> getAuditLogs(UUID actorUserId, UUID actorEmployeeId,
            AuditAction action, AuditResourceType resourceType, UUID resourceId, AuditStatus status,
            LocalDate from, LocalDate to, int page, int size, String sort, String direction);
}
