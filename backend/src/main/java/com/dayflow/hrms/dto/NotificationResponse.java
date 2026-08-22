package com.dayflow.hrms.dto;

import com.dayflow.hrms.entity.Notification;
import com.dayflow.hrms.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO representing notification details.
 */
@Schema(description = "Response containing notification details")
public class NotificationResponse {

    @Schema(description = "Notification unique ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Employee ID", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID employeeId;

    @Schema(description = "Type/Category of notification", example = "LEAVE_APPROVED")
    private NotificationType type;

    @Schema(description = "Notification title", example = "Leave Approved")
    private String title;

    @Schema(description = "Notification body message", example = "Your leave request for 2026-09-01 to 2026-09-03 has been approved.")
    private String message;

    @Schema(description = "Flag indicating whether notification has been read", example = "false")
    private boolean isRead;

    @Schema(description = "Timestamp when notification was created")
    private Instant createdAt;

    @Schema(description = "Timestamp when notification was marked as read")
    private Instant readAt;

    @Schema(description = "Optional domain reference type (e.g. LEAVE_REQUEST, PAYROLL)", example = "LEAVE_REQUEST")
    private String referenceType;

    @Schema(description = "Optional domain reference ID", example = "123e4567-e89b-12d3-a456-426614174002")
    private UUID referenceId;

    public NotificationResponse() {
    }

    public NotificationResponse(UUID id, UUID employeeId, NotificationType type, String title,
                                String message, boolean isRead, Instant createdAt, Instant readAt,
                                String referenceType, UUID referenceId) {
        this.id = id;
        this.employeeId = employeeId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.readAt = readAt;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
    }

    public static NotificationResponse fromEntity(Notification entity) {
        if (entity == null) {
            return null;
        }

        UUID empId = entity.getEmployee() != null ? entity.getEmployee().getId() : null;

        return NotificationResponse.builder()
                .id(entity.getId())
                .employeeId(empId)
                .type(entity.getType())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .isRead(entity.isRead())
                .createdAt(entity.getCreatedAt())
                .readAt(entity.getReadAt())
                .referenceType(entity.getReferenceType())
                .referenceId(entity.getReferenceId())
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

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public void setReadAt(Instant readAt) {
        this.readAt = readAt;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public UUID getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(UUID referenceId) {
        this.referenceId = referenceId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID employeeId;
        private NotificationType type;
        private String title;
        private String message;
        private boolean isRead;
        private Instant createdAt;
        private Instant readAt;
        private String referenceType;
        private UUID referenceId;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder employeeId(UUID employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public Builder type(NotificationType type) {
            this.type = type;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder isRead(boolean isRead) {
            this.isRead = isRead;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder readAt(Instant readAt) {
            this.readAt = readAt;
            return this;
        }

        public Builder referenceType(String referenceType) {
            this.referenceType = referenceType;
            return this;
        }

        public Builder referenceId(UUID referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public NotificationResponse build() {
            return new NotificationResponse(id, employeeId, type, title, message, isRead, createdAt, readAt, referenceType, referenceId);
        }
    }
}
