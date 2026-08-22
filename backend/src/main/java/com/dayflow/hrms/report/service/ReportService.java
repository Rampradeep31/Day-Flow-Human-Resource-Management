package com.dayflow.hrms.report.service;

import com.dayflow.hrms.dto.PageResponse;
import com.dayflow.hrms.entity.EmploymentStatus;
import com.dayflow.hrms.entity.LeaveStatus;
import com.dayflow.hrms.entity.LeaveType;
import com.dayflow.hrms.report.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReportService {
    PageResponse<EmployeeReportResponse> getEmployeeReport(String department, EmploymentStatus status,
            String designation, LocalDate from, LocalDate to, int page, int size, String sort, String direction);

    PageResponse<AttendanceReportResponse> getAttendanceReport(UUID employeeId, LocalDate from, LocalDate to,
            int page, int size, String sort, String direction);

    PageResponse<LeaveReportResponse> getLeaveReport(UUID employeeId, LeaveType leaveType, LeaveStatus status,
            LocalDate from, LocalDate to, String department, int page, int size, String sort, String direction);

    PageResponse<PayrollReportResponse> getPayrollReport(UUID employeeId, String department,
            EmploymentStatus employmentStatus, LocalDate from, LocalDate to, int page, int size,
            String sort, String direction);

    List<DepartmentReportResponse> getDepartmentReport();
}
