package com.dayflow.hrms.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

/**
 * Standard API Error Response DTO for structured error handling.
 */
@Schema(description = "Standard API error response payload")
public class ApiErrorResponse {

    @Schema(description = "Indicates failure", example = "false")
    private boolean success;

    @Schema(description = "Human-readable error summary", example = "Validation failed")
    private String message;

    @Schema(description = "Standardized error code", example = "VALIDATION_FAILED")
    private String errorCode;

    @Schema(description = "Timestamp when error occurred")
    private Instant timestamp;

    @Schema(description = "Optional field-specific error details")
    private Map<String, String> errors;

    public ApiErrorResponse() {
        this.timestamp = Instant.now();
    }

    public ApiErrorResponse(boolean success, String message, String errorCode, Instant timestamp) {
        this(success, message, errorCode, timestamp, null);
    }

    public ApiErrorResponse(boolean success, String message, String errorCode, Instant timestamp, Map<String, String> errors) {
        this.success = success;
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
        this.errors = errors;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public static class Builder {
        private boolean success = false;
        private String message;
        private String errorCode;
        private Instant timestamp = Instant.now();
        private Map<String, String> errors;

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder errors(Map<String, String> errors) {
            this.errors = errors;
            return this;
        }

        public ApiErrorResponse build() {
            return new ApiErrorResponse(success, message, errorCode, timestamp, errors);
        }
    }
}
