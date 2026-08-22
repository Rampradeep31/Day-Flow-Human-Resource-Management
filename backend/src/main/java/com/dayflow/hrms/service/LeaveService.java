package com.dayflow.hrms.service;

import com.dayflow.hrms.dto.CreateLeaveRequest;
import com.dayflow.hrms.dto.LeaveResponse;
import com.dayflow.hrms.dto.PageResponse;
import com.dayflow.hrms.dto.ReviewLeaveRequest;
import com.dayflow.hrms.entity.LeaveStatus;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Service interface for Leave & Time-Off operations.
 */
public interface LeaveService {

    LeaveResponse applyLeave(CreateLeaveRequest request);

    PageResponse<LeaveResponse> getOwnLeaves(Pageable pageable);

    LeaveResponse getLeaveById(UUID leaveId);

    PageResponse<LeaveResponse> getLeaves(UUID employeeId, LeaveStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);

    PageResponse<LeaveResponse> getEmployeeLeaves(UUID employeeId, Pageable pageable);

    LeaveResponse approveLeave(UUID leaveId, ReviewLeaveRequest request);

    LeaveResponse rejectLeave(UUID leaveId, ReviewLeaveRequest request);
}
