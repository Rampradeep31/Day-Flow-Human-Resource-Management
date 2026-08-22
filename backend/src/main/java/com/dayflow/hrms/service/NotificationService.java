package com.dayflow.hrms.service;

import com.dayflow.hrms.dto.NotificationResponse;
import com.dayflow.hrms.dto.PageResponse;
import com.dayflow.hrms.dto.UnreadCountResponse;
import com.dayflow.hrms.entity.Employee;
import com.dayflow.hrms.entity.Notification;
import com.dayflow.hrms.entity.NotificationType;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for Notification Management operations.
 */
public interface NotificationService {

    Notification createNotification(Employee employee, NotificationType type, String title,
                                   String message, String referenceType, UUID referenceId);

    PageResponse<NotificationResponse> getMyNotifications(Pageable pageable);

    UnreadCountResponse getUnreadCount();

    NotificationResponse markAsRead(UUID notificationId);
}
