package com.dayflow.hrms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO for HR/Admin leave approval and rejection decisions.
 */
@Schema(description = "Payload for reviewing (approving/rejecting) a leave request")
public class ReviewLeaveRequest {

    @Size(max = 500, message = "Review comment cannot exceed 500 characters")
    @Schema(description = "Optional reviewer comments or justification", example = "Approved based on available leave quota.")
    private String comment;

    public ReviewLeaveRequest() {
    }

    public ReviewLeaveRequest(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String comment;

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public ReviewLeaveRequest build() {
            return new ReviewLeaveRequest(comment);
        }
    }
}
