package com.dayflow.hrms.dto;

import com.dayflow.hrms.entity.Document;
import com.dayflow.hrms.entity.DocumentType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO representing employee document metadata.
 */
@Schema(description = "Response containing employee document metadata")
public class DocumentResponse {

    @Schema(description = "Document unique ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Employee ID", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID employeeId;

    @Schema(description = "Employee Code", example = "EMP101")
    private String employeeCode;

    @Schema(description = "Employee full name", example = "Dwight Schrute")
    private String employeeName;

    @Schema(description = "Original safe file name", example = "Dwight_Resume.pdf")
    private String fileName;

    @Schema(description = "MIME content type", example = "application/pdf")
    private String contentType;

    @Schema(description = "File size in bytes", example = "245678")
    private Long fileSize;

    @Schema(description = "Category / type of document", example = "RESUME")
    private DocumentType documentType;

    @Schema(description = "Short-lived secure signed URL for document retrieval", example = "https://...supabase.co/storage/v1/object/sign/...")
    private String downloadUrl;

    @Schema(description = "Timestamp when document was uploaded")
    private Instant createdAt;

    @Schema(description = "Timestamp when document metadata was last updated")
    private Instant updatedAt;

    public DocumentResponse() {
    }

    public DocumentResponse(UUID id, UUID employeeId, String employeeCode, String employeeName,
                            String fileName, String contentType, Long fileSize,
                            DocumentType documentType, String downloadUrl,
                            Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.documentType = documentType;
        this.downloadUrl = downloadUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static DocumentResponse fromEntity(Document entity) {
        return fromEntity(entity, null);
    }

    public static DocumentResponse fromEntity(Document entity, String downloadUrl) {
        if (entity == null) {
            return null;
        }

        UUID empId = null;
        String empCode = null;
        String empName = null;

        if (entity.getEmployee() != null) {
            empId = entity.getEmployee().getId();
            empCode = entity.getEmployee().getEmployeeCode();
            empName = entity.getEmployee().getFullName();
        }

        return DocumentResponse.builder()
                .id(entity.getId())
                .employeeId(empId)
                .employeeCode(empCode)
                .employeeName(empName)
                .fileName(entity.getFileName())
                .contentType(entity.getContentType())
                .fileSize(entity.getFileSize())
                .documentType(entity.getDocumentType())
                .downloadUrl(downloadUrl)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
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

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID employeeId;
        private String employeeCode;
        private String employeeName;
        private String fileName;
        private String contentType;
        private Long fileSize;
        private DocumentType documentType;
        private String downloadUrl;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder employeeId(UUID employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public Builder employeeCode(String employeeCode) {
            this.employeeCode = employeeCode;
            return this;
        }

        public Builder employeeName(String employeeName) {
            this.employeeName = employeeName;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public Builder documentType(DocumentType documentType) {
            this.documentType = documentType;
            return this;
        }

        public Builder downloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public DocumentResponse build() {
            return new DocumentResponse(id, employeeId, employeeCode, employeeName, fileName,
                    contentType, fileSize, documentType, downloadUrl, createdAt, updatedAt);
        }
    }
}
