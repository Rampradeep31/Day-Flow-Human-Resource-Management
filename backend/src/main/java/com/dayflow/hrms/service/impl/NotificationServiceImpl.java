package com.dayflow.hrms.service.impl;

import com.dayflow.hrms.audit.enums.AuditAction;
import com.dayflow.hrms.audit.enums.AuditResourceType;
import com.dayflow.hrms.audit.service.AuditLogService;
import com.dayflow.hrms.dto.NotificationResponse;
import com.dayflow.hrms.dto.PageResponse;
import com.dayflow.hrms.dto.UnreadCountResponse;
import com.dayflow.hrms.entity.Employee;
import com.dayflow.hrms.entity.Notification;
import com.dayflow.hrms.entity.NotificationType;
import com.dayflow.hrms.exception.ResourceNotFoundException;
import com.dayflow.hrms.repository.EmployeeRepository;
import com.dayflow.hrms.repository.NotificationRepository;
import com.dayflow.hrms.security.SecurityUtils;
import com.dayflow.hrms.security.UserPrincipal;
import com.dayflow.hrms.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of NotificationService managing user notifications,
 * unread counters, and ownership enforcement.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final EmployeeRepository employeeRepository;
    private final AuditLogService auditLogService;

    public NotificationServiceImpl(NotificationRepository notificationRepository, EmployeeRepository employeeRepository,
                                   AuditLogService auditLogService) {
        this.notificationRepository = notificationRepository;
        this.employeeRepository = employeeRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public Notification createNotification(Employee employee, NotificationType type, String title,
                                          String message, String referenceType, UUID referenceId) {
        if (employee == null) {
            log.warn("Cannot create notification for null employee");
            return null;
        }

        Notification notification = Notification.builder()
                .employee(employee)
                .type(type != null ? type : NotificationType.GENERAL)
                .title(title)
                .message(message)
                .isRead(false)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Created notification [{}] for employee {}: {}", saved.getType(), employee.getEmployeeCode(), saved.getTitle());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getMyNotifications(Pageable pageable) {
        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));

        Employee employee = employeeRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found for user: " + currentUserId));

        Page<Notification> page = notificationRepository.findByEmployeeIdWithDetails(employee.getId(), pageable);
        return PageResponse.of(page.map(NotificationResponse::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public UnreadCountResponse getUnreadCount() {
        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));

        Employee employee = employeeRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found for user: " + currentUserId));

        long count = notificationRepository.countByEmployeeIdAndIsReadFalse(employee.getId());
        return new UnreadCountResponse(count);
    }

    @Override
    public NotificationResponse markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findByIdWithDetails(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));

        UserPrincipal principal = SecurityUtils.getCurrentUserPrincipal()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));

        boolean isOwner = notification.getEmployee().getUser() != null &&
                notification.getEmployee().getUser().getId().equals(principal.getId());

        if (!isOwner) {
            log.warn("Access denied: User {} attempted to mark notification {} as read",
                    principal.getEmail(), notificationId);
            throw new AccessDeniedException("You do not have permission to modify this notification");
        }

        boolean changed = !notification.isRead();
        notification.markAsRead();
        Notification saved = notificationRepository.save(notification);
        log.info("Notification {} marked as read for employee {}", notificationId, saved.getEmployee().getEmployeeCode());

        if (changed) {
            auditLogService.logSuccess(AuditAction.NOTIFICATION_READ, AuditResourceType.NOTIFICATION,
                    saved.getId(), "Notification marked read");
        }

        return NotificationResponse.fromEntity(saved);
    }
}
