package com.dayflow.hrms.dto;

import java.time.Instant;

/**
 * Standard API Error Response DTO for structured error handling.
 */
public class ApiErrorResponse {
    private boolean success;
    private String message;
    private String errorCode;
    private Instant timestamp;

    public ApiErrorResponse() {
        this.timestamp = Instant.now();
    }

    public ApiErrorResponse(boolean success, String message, String errorCode, Instant timestamp) {
        this.success = success;
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
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

    public static class Builder {
        private boolean success = false;
        private String message;
        private String errorCode;
        private Instant timestamp = Instant.now();

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

        public ApiErrorResponse build() {
            return new ApiErrorResponse(success, message, errorCode, timestamp);
        }
    }
}
