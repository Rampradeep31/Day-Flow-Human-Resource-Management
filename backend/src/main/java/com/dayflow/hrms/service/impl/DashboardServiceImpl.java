package com.dayflow.hrms.service.impl;

import com.dayflow.hrms.dto.AdminDashboardResponse;
import com.dayflow.hrms.dto.EmployeeDashboardResponse;
import com.dayflow.hrms.dto.NotificationResponse;
import com.dayflow.hrms.entity.*;
import com.dayflow.hrms.exception.ResourceNotFoundException;
import com.dayflow.hrms.repository.*;
import com.dayflow.hrms.security.SecurityUtils;
import com.dayflow.hrms.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of DashboardService that aggregates data from existing modules.
 * All methods are read-only and produce no side effects.
 */
@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardServiceImpl.class);

    private static final int RECENT_NOTIFICATIONS_LIMIT = 5;
    private static final int RECENT_ACTIVITIES_LIMIT = 10;

    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final PayrollRepository payrollRepository;
    private final NotificationRepository notificationRepository;

    public DashboardServiceImpl(
            EmployeeRepository employeeRepository,
            LeaveRequestRepository leaveRequestRepository,
            PayrollRepository payrollRepository,
            NotificationRepository notificationRepository) {
        this.employeeRepository = employeeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.payrollRepository = payrollRepository;
        this.notificationRepository = notificationRepository;
    }

    // ────────────────────────────────────────────────────────────
    // Employee Dashboard
    // ────────────────────────────────────────────────────────────

    @Override
    public EmployeeDashboardResponse getEmployeeDashboard() {
        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));

        Employee employee = employeeRepository.findByUserIdWithDetails(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found for authenticated user"));

        EmployeeDashboardResponse response = new EmployeeDashboardResponse();

        // 1. Employee info
        response.setEmployee(buildEmployeeInfo(employee));

        // 2. Attendance (module not yet implemented)
        response.setAttendance(EmployeeDashboardResponse.AttendanceInfo.notAvailable());

        // 3. Leave summary
        response.setLeaveSummary(buildLeaveSummary(employee.getId()));

        // 4. Recent notifications (latest 5)
        List<Notification> recentNotifications = notificationRepository.findRecentByEmployeeId(
                employee.getId(), PageRequest.of(0, RECENT_NOTIFICATIONS_LIMIT));
        response.setRecentNotifications(
                recentNotifications.stream()
                        .map(NotificationResponse::fromEntity)
                        .collect(Collectors.toList()));

        // 5. Unread notification count
        response.setUnreadNotificationsCount(
                notificationRepository.countByEmployeeIdAndIsReadFalse(employee.getId()));

        // 6. Payroll summary
        response.setPayroll(buildEmployeePayrollSummary(employee.getId()));

        log.info("Employee dashboard retrieved for employee {}", employee.getEmployeeCode());
        return response;
    }

    private EmployeeDashboardResponse.EmployeeInfo buildEmployeeInfo(Employee employee) {
        String email = employee.getUser() != null ? employee.getUser().getEmail() : null;
        return new EmployeeDashboardResponse.EmployeeInfo(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFullName(),
                email,
                employee.getDepartment(),
                employee.getDesignation(),
                employee.getEmploymentStatus() != null ? employee.getEmploymentStatus().name() : null,
                employee.getJoiningDate()
        );
    }

    private EmployeeDashboardResponse.LeaveSummary buildLeaveSummary(UUID employeeId) {
        long pending = leaveRequestRepository.countByEmployeeIdAndStatus(employeeId, LeaveStatus.PENDING);
        long approved = leaveRequestRepository.countByEmployeeIdAndStatus(employeeId, LeaveStatus.APPROVED);
        long rejected = leaveRequestRepository.countByEmployeeIdAndStatus(employeeId, LeaveStatus.REJECTED);
        long total = pending + approved + rejected;
        return new EmployeeDashboardResponse.LeaveSummary(total, pending, approved, rejected);
    }

    private EmployeeDashboardResponse.PayrollSummary buildEmployeePayrollSummary(UUID employeeId) {
        Optional<Payroll> payrollOpt = payrollRepository.findByEmployeeId(employeeId);
        if (payrollOpt.isEmpty()) {
            return null;
        }
        Payroll payroll = payrollOpt.get();
        return new EmployeeDashboardResponse.PayrollSummary(
                payroll.getBaseSalary(),
                payroll.getAllowances(),
                payroll.getDeductions(),
                payroll.getNetSalary()
        );
    }

    // ────────────────────────────────────────────────────────────
    // Admin Dashboard
    // ────────────────────────────────────────────────────────────

    @Override
    public AdminDashboardResponse getAdminDashboard() {
        AdminDashboardResponse response = new AdminDashboardResponse();

        // 1. Employee statistics
        long totalEmployees = employeeRepository.count();
        long activeEmployees = employeeRepository.countByEmploymentStatus(EmploymentStatus.ACTIVE);
        response.setEmployeeStatistics(new AdminDashboardResponse.EmployeeStatistics(totalEmployees, activeEmployees));

        // 2. Attendance statistics (module not yet implemented)
        response.setAttendanceStatistics(AdminDashboardResponse.AttendanceStatistics.notAvailable());

        // 3. Leave statistics
        LocalDate today = LocalDate.now();
        long onLeaveToday = leaveRequestRepository.countEmployeesOnLeaveToday(today);
        long pendingRequests = leaveRequestRepository.countByStatus(LeaveStatus.PENDING);
        response.setLeaveStatistics(new AdminDashboardResponse.LeaveStatistics(onLeaveToday, pendingRequests));

        // 4. Department statistics
        List<Object[]> deptCounts = employeeRepository.countByDepartment();
        List<AdminDashboardResponse.DepartmentStat> departmentStats = deptCounts.stream()
                .map(row -> new AdminDashboardResponse.DepartmentStat(
                        (String) row[0],
                        (Long) row[1]))
                .collect(Collectors.toList());
        response.setDepartmentStatistics(departmentStats);

        // 5. Recent activities (from recent leave requests — lightweight substitute for Audit Logs)
        response.setRecentActivities(buildRecentActivities());

        // 6. Payroll summary (aggregate)
        response.setPayrollSummary(buildAdminPayrollSummary());

        log.info("Admin dashboard retrieved");
        return response;
    }

    private List<AdminDashboardResponse.RecentActivity> buildRecentActivities() {
        Page<LeaveRequest> recentLeaves = leaveRequestRepository.findRecentWithDetails(
                PageRequest.of(0, RECENT_ACTIVITIES_LIMIT));

        List<AdminDashboardResponse.RecentActivity> activities = new ArrayList<>();
        for (LeaveRequest lr : recentLeaves.getContent()) {
            String employeeName = lr.getEmployee() != null ? lr.getEmployee().getFullName() : "Unknown";
            String type;
            String description;

            switch (lr.getStatus()) {
                case PENDING:
                    type = "LEAVE_SUBMITTED";
                    description = employeeName + " submitted a " + lr.getLeaveType() + " leave request (" +
                            lr.getStartDate() + " to " + lr.getEndDate() + ")";
                    break;
                case APPROVED:
                    type = "LEAVE_APPROVED";
                    description = employeeName + "'s " + lr.getLeaveType() + " leave (" +
                            lr.getStartDate() + " to " + lr.getEndDate() + ") was approved";
                    break;
                case REJECTED:
                    type = "LEAVE_REJECTED";
                    description = employeeName + "'s " + lr.getLeaveType() + " leave (" +
                            lr.getStartDate() + " to " + lr.getEndDate() + ") was rejected";
                    break;
                default:
                    type = "LEAVE_UPDATE";
                    description = employeeName + "'s leave request was updated";
            }

            activities.add(new AdminDashboardResponse.RecentActivity(type, description, lr.getUpdatedAt()));
        }
        return activities;
    }

    private AdminDashboardResponse.PayrollSummary buildAdminPayrollSummary() {
        List<Object[]> results = payrollRepository.getAggregatePayrollSummary();
        if (results == null || results.isEmpty()) {
            return new AdminDashboardResponse.PayrollSummary(
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        Object[] row = results.get(0);
        if (row == null || row.length < 4) {
            return new AdminDashboardResponse.PayrollSummary(
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        return new AdminDashboardResponse.PayrollSummary(
                toBigDecimal(row[0]),
                toBigDecimal(row[1]),
                toBigDecimal(row[2]),
                toBigDecimal(row[3])
        );
    }

    private static BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Number) return BigDecimal.valueOf(((Number) value).doubleValue());
        return BigDecimal.ZERO;
    }
}
