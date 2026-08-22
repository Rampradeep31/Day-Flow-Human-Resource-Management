package com.dayflow.hrms.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO representing unread notification count.
 */
@Schema(description = "Response containing unread notification count")
public class UnreadCountResponse {

    @Schema(description = "Count of unread notifications", example = "3")
    private long unreadCount;

    public UnreadCountResponse() {
    }

    public UnreadCountResponse(long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }
}
