package com.dayflow.hrms.service.impl;

import com.dayflow.hrms.dto.CreateLeaveRequest;
import com.dayflow.hrms.dto.LeaveResponse;
import com.dayflow.hrms.dto.PageResponse;
import com.dayflow.hrms.dto.ReviewLeaveRequest;
import com.dayflow.hrms.entity.*;
import com.dayflow.hrms.exception.BadRequestException;
import com.dayflow.hrms.exception.DuplicateResourceException;
import com.dayflow.hrms.exception.ResourceNotFoundException;
import com.dayflow.hrms.repository.EmployeeRepository;
import com.dayflow.hrms.repository.LeaveRequestRepository;
import com.dayflow.hrms.repository.UserRepository;
import com.dayflow.hrms.security.SecurityUtils;
import com.dayflow.hrms.security.UserPrincipal;
import com.dayflow.hrms.service.LeaveService;
import com.dayflow.hrms.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of LeaveService providing leave request workflow,
 * overlap validation, and status lifecycle enforcement.
 */
@Service
@Transactional
public class LeaveServiceImpl implements LeaveService {

    private static final Logger log = LoggerFactory.getLogger(LeaveServiceImpl.class);

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public LeaveServiceImpl(
            LeaveRequestRepository leaveRequestRepository,
            EmployeeRepository employeeRepository,
            UserRepository userRepository,
            NotificationService notificationService) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public LeaveResponse applyLeave(CreateLeaveRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));

        Employee employee = employeeRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found for authenticated user: " + currentUserId));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestException("Start date (" + request.getStartDate() + ") cannot be after end date (" + request.getEndDate() + ")");
        }

        // Overlap protection: Check against existing PENDING and APPROVED leave requests for this employee
        List<LeaveRequest> overlapping = leaveRequestRepository.findActiveOverlappingRequests(
                employee.getId(),
                request.getStartDate(),
                request.getEndDate()
        );

        if (!overlapping.isEmpty()) {
            LeaveRequest conflict = overlapping.get(0);
            throw new DuplicateResourceException(
                    "Leave request overlaps with an existing " + conflict.getStatus() +
                    " leave request (" + conflict.getStartDate() + " to " + conflict.getEndDate() + ")"
            );
        }

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .employee(employee)
                .leaveType(request.getLeaveType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .remarks(request.getRemarks() != null ? request.getRemarks().trim() : null)
                .status(LeaveStatus.PENDING)
                .build();

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        log.info("Employee {} submitted new {} leave request ({} to {})",
                employee.getEmployeeCode(), saved.getLeaveType(), saved.getStartDate(), saved.getEndDate());

        return LeaveResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<LeaveResponse> getOwnLeaves(Pageable pageable) {
        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));

        Employee employee = employeeRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found for authenticated user: " + currentUserId));

        Page<LeaveRequest> page = leaveRequestRepository.findByEmployeeIdWithDetails(employee.getId(), pageable);
        return PageResponse.of(page.map(LeaveResponse::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveResponse getLeaveById(UUID leaveId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findByIdWithDetails(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with ID: " + leaveId));

        UserPrincipal principal = SecurityUtils.getCurrentUserPrincipal()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));

        boolean isHrOrAdmin = principal.getRoleNames().contains("HR") || principal.getRoleNames().contains("ADMIN");
        boolean isOwner = leaveRequest.getEmployee().getUser() != null &&
                leaveRequest.getEmployee().getUser().getId().equals(principal.getId());

        if (!isHrOrAdmin && !isOwner) {
            log.warn("Access denied: User {} attempted to view leave request of employee {}",
                    principal.getEmail(), leaveRequest.getEmployee().getEmployeeCode());
            throw new AccessDeniedException("You do not have permission to view other employee leave requests");
        }

        return LeaveResponse.fromEntity(leaveRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<LeaveResponse> getLeaves(
            UUID employeeId,
            LeaveStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date filter cannot be after end date filter");
        }

        Page<LeaveRequest> page = leaveRequestRepository.findWithFilters(employeeId, status, startDate, endDate, pageable);
        return PageResponse.of(page.map(LeaveResponse::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<LeaveResponse> getEmployeeLeaves(UUID employeeId, Pageable pageable) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }

        Page<LeaveRequest> page = leaveRequestRepository.findByEmployeeIdWithDetails(employeeId, pageable);
        return PageResponse.of(page.map(LeaveResponse::fromEntity));
    }

    @Override
    public LeaveResponse approveLeave(UUID leaveId, ReviewLeaveRequest request) {
        LeaveRequest leaveRequest = leaveRequestRepository.findByIdWithDetails(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with ID: " + leaveId));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new DuplicateResourceException("Leave request is already " + leaveRequest.getStatus() + " and cannot be re-processed");
        }

        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));

        User reviewer = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer user account not found with ID: " + currentUserId));

        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setReviewedBy(reviewer);
        leaveRequest.setReviewedAt(Instant.now());
        if (request != null && request.getComment() != null) {
            leaveRequest.setReviewComment(request.getComment().trim());
        }

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        log.info("Leave request {} for employee {} APPROVED by reviewer {}",
                saved.getId(), saved.getEmployee().getEmployeeCode(), reviewer.getEmail());

        // Trigger LEAVE_APPROVED notification
        try {
            notificationService.createNotification(
                    saved.getEmployee(),
                    NotificationType.LEAVE_APPROVED,
                    "Leave Approved",
                    "Your " + saved.getLeaveType() + " leave request (" + saved.getStartDate() + " to " + saved.getEndDate() + ") has been approved.",
                    "LEAVE_REQUEST",
                    saved.getId()
            );
        } catch (Exception e) {
            log.warn("Failed to create notification for approved leave {}: {}", saved.getId(), e.getMessage());
        }

        return LeaveResponse.fromEntity(saved);
    }

    @Override
    public LeaveResponse rejectLeave(UUID leaveId, ReviewLeaveRequest request) {
        LeaveRequest leaveRequest = leaveRequestRepository.findByIdWithDetails(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with ID: " + leaveId));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new DuplicateResourceException("Leave request is already " + leaveRequest.getStatus() + " and cannot be re-processed");
        }

        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));

        User reviewer = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer user account not found with ID: " + currentUserId));

        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setReviewedBy(reviewer);
        leaveRequest.setReviewedAt(Instant.now());
        if (request != null && request.getComment() != null) {
            leaveRequest.setReviewComment(request.getComment().trim());
        }

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        log.info("Leave request {} for employee {} REJECTED by reviewer {}",
                saved.getId(), saved.getEmployee().getEmployeeCode(), reviewer.getEmail());

        // Trigger LEAVE_REJECTED notification
        try {
            notificationService.createNotification(
                    saved.getEmployee(),
                    NotificationType.LEAVE_REJECTED,
                    "Leave Rejected",
                    "Your " + saved.getLeaveType() + " leave request (" + saved.getStartDate() + " to " + saved.getEndDate() + ") has been rejected.",
                    "LEAVE_REQUEST",
                    saved.getId()
            );
        } catch (Exception e) {
            log.warn("Failed to create notification for rejected leave {}: {}", saved.getId(), e.getMessage());
        }

        return LeaveResponse.fromEntity(saved);
    }
}
