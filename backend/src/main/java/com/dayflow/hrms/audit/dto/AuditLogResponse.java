package com.dayflow.hrms.audit.dto;

import com.dayflow.hrms.audit.entity.AuditLog;
import com.dayflow.hrms.audit.enums.AuditAction;
import com.dayflow.hrms.audit.enums.AuditResourceType;
import com.dayflow.hrms.audit.enums.AuditStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Immutable audit event")
public record AuditLogResponse(
        UUID id,
        UUID actorUserId,
        UUID actorEmployeeId,
        String actorName,
        AuditAction action,
        AuditResourceType resourceType,
        UUID resourceId,
        String description,
        AuditStatus status,
        String ipAddress,
        String userAgent,
        Instant createdAt) {

    public static AuditLogResponse fromEntity(AuditLog log) {
        return new AuditLogResponse(log.getId(), log.getActorUserId(), log.getActorEmployeeId(),
                log.getActorName(), log.getAction(), log.getResourceType(), log.getResourceId(),
                log.getDescription(), log.getStatus(), log.getIpAddress(), log.getUserAgent(),
                log.getCreatedAt());
    }
}
