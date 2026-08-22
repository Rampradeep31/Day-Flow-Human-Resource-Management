package com.dayflow.hrms.report.dto;

import com.dayflow.hrms.entity.LeaveRequest;
import com.dayflow.hrms.entity.LeaveStatus;
import com.dayflow.hrms.entity.LeaveType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Leave report row")
public record LeaveReportResponse(
        UUID id,
        UUID employeeId,
        String employeeCode,
        String employeeName,
        String department,
        LeaveType leaveType,
        LocalDate startDate,
        LocalDate endDate,
        long durationDays,
        LeaveStatus status,
        String reason,
        Instant createdAt) {

    public static LeaveReportResponse fromEntity(LeaveRequest leave) {
        return new LeaveReportResponse(leave.getId(), leave.getEmployee().getId(),
                leave.getEmployee().getEmployeeCode(), leave.getEmployee().getFullName(),
                leave.getEmployee().getDepartment(), leave.getLeaveType(), leave.getStartDate(),
                leave.getEndDate(), leave.getDurationInDays(), leave.getStatus(), leave.getRemarks(),
                leave.getCreatedAt());
    }
}
