package com.dayflow.hrms.report.service;

import com.dayflow.hrms.dto.PageResponse;
import com.dayflow.hrms.entity.*;
import com.dayflow.hrms.exception.BadRequestException;
import com.dayflow.hrms.exception.ResourceNotFoundException;
import com.dayflow.hrms.report.dto.*;
import com.dayflow.hrms.repository.EmployeeRepository;
import com.dayflow.hrms.repository.LeaveRequestRepository;
import com.dayflow.hrms.repository.PayrollRepository;
import com.dayflow.hrms.security.SecurityUtils;
import com.dayflow.hrms.security.UserPrincipal;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final Map<String, String> EMPLOYEE_SORTS = Map.of(
            "employeeCode", "employeeCode", "employeeName", "firstName", "department", "department",
            "designation", "designation", "employmentStatus", "employmentStatus",
            "joiningDate", "joiningDate", "createdAt", "createdAt");
    private static final Map<String, String> ATTENDANCE_SORTS = Map.of(
            "date", "date", "employeeName", "employeeName", "attendanceStatus", "attendanceStatus");
    private static final Map<String, String> LEAVE_SORTS = Map.of(
            "createdAt", "createdAt", "startDate", "startDate", "endDate", "endDate",
            "status", "status", "leaveType", "leaveType", "employeeName", "employee.firstName",
            "department", "employee.department");
    private static final Map<String, String> PAYROLL_SORTS = Map.of(
            "createdAt", "createdAt", "updatedAt", "updatedAt", "employeeName", "employee.firstName",
            "department", "employee.department", "baseSalary", "baseSalary", "netSalary", "netSalary");

    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final PayrollRepository payrollRepository;

    public ReportServiceImpl(EmployeeRepository employeeRepository,
                             LeaveRequestRepository leaveRequestRepository,
                             PayrollRepository payrollRepository) {
        this.employeeRepository = employeeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.payrollRepository = payrollRepository;
    }

    @Override
    public PageResponse<EmployeeReportResponse> getEmployeeReport(String department, EmploymentStatus status,
            String designation, LocalDate from, LocalDate to, int page, int size, String sort, String direction) {
        validateDateRange(from, to);
        Pageable pageable = pageable(page, size, sort, direction, EMPLOYEE_SORTS);
        UUID employeeScope = organizationReporter() ? null : currentEmployee().getId();
        String cleanDepartment = clean(department);
        String cleanDesignation = clean(designation);

        Specification<Employee> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (employeeScope != null) predicates.add(cb.equal(root.get("id"), employeeScope));
            if (cleanDepartment != null) predicates.add(cb.equal(cb.lower(root.get("department")), cleanDepartment.toLowerCase(Locale.ROOT)));
            if (status != null) predicates.add(cb.equal(root.get("employmentStatus"), status));
            if (cleanDesignation != null) predicates.add(cb.equal(cb.lower(root.get("designation")), cleanDesignation.toLowerCase(Locale.ROOT)));
            if (from != null) predicates.add(cb.greaterThanOrEqualTo(root.get("joiningDate"), from));
            if (to != null) predicates.add(cb.lessThanOrEqualTo(root.get("joiningDate"), to));
            return cb.and(predicates.toArray(Predicate[]::new));
        };

        return PageResponse.of(employeeRepository.findAll(specification, pageable).map(EmployeeReportResponse::fromEntity));
    }

    @Override
    public PageResponse<AttendanceReportResponse> getAttendanceReport(UUID employeeId, LocalDate from, LocalDate to,
            int page, int size, String sort, String direction) {
        validateDateRange(from, to);
        pageable(page, size, sort, direction, ATTENDANCE_SORTS);
        resolveEmployeeScope(employeeId);
        // This checkout has no attendance entity/table. Reporting must not invent attendance data.
        return new PageResponse<>(List.of(), page, size, 0, 0, true);
    }

    @Override
    public PageResponse<LeaveReportResponse> getLeaveReport(UUID employeeId, LeaveType leaveType, LeaveStatus status,
            LocalDate from, LocalDate to, String department, int page, int size, String sort, String direction) {
        validateDateRange(from, to);
        Pageable pageable = pageable(page, size, sort, direction, LEAVE_SORTS);
        UUID scopedEmployeeId = resolveEmployeeScope(employeeId);
        String cleanDepartment = clean(department);

        Specification<LeaveRequest> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (scopedEmployeeId != null) predicates.add(cb.equal(root.get("employee").get("id"), scopedEmployeeId));
            if (leaveType != null) predicates.add(cb.equal(root.get("leaveType"), leaveType));
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (from != null) predicates.add(cb.greaterThanOrEqualTo(root.get("endDate"), from));
            if (to != null) predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), to));
            if (cleanDepartment != null) predicates.add(cb.equal(cb.lower(root.get("employee").get("department")), cleanDepartment.toLowerCase(Locale.ROOT)));
            return cb.and(predicates.toArray(Predicate[]::new));
        };

        Page<LeaveReportResponse> result = leaveRequestRepository.findAll(specification, pageable)
                .map(LeaveReportResponse::fromEntity);
        return PageResponse.of(result);
    }

    @Override
    public PageResponse<PayrollReportResponse> getPayrollReport(UUID employeeId, String department,
            EmploymentStatus employmentStatus, LocalDate from, LocalDate to, int page, int size,
            String sort, String direction) {
        validateDateRange(from, to);
        Pageable pageable = pageable(page, size, sort, direction, PAYROLL_SORTS);
        if (employeeId != null) requireEmployee(employeeId);
        String cleanDepartment = clean(department);

        Specification<Payroll> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (employeeId != null) predicates.add(cb.equal(root.get("employee").get("id"), employeeId));
            if (cleanDepartment != null) predicates.add(cb.equal(cb.lower(root.get("employee").get("department")), cleanDepartment.toLowerCase(Locale.ROOT)));
            if (employmentStatus != null) predicates.add(cb.equal(root.get("employee").get("employmentStatus"), employmentStatus));
            if (from != null) predicates.add(cb.greaterThanOrEqualTo(root.get("updatedAt"), from.atStartOfDay().toInstant(ZoneOffset.UTC)));
            if (to != null) predicates.add(cb.lessThan(root.get("updatedAt"), to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)));
            return cb.and(predicates.toArray(Predicate[]::new));
        };

        return PageResponse.of(payrollRepository.findAll(specification, pageable).map(PayrollReportResponse::fromEntity));
    }

    @Override
    public List<DepartmentReportResponse> getDepartmentReport() {
        Map<String, DepartmentAccumulator> departments = new LinkedHashMap<>();
        for (Object[] row : employeeRepository.getDepartmentEmployeeReport()) {
            DepartmentAccumulator value = departments.computeIfAbsent((String) row[0], ignored -> new DepartmentAccumulator());
            value.employeeCount = number(row[1]);
            value.activeEmployeeCount = number(row[2]);
        }
        for (Object[] row : leaveRequestRepository.getDepartmentLeaveReport()) {
            DepartmentAccumulator value = departments.computeIfAbsent((String) row[0], ignored -> new DepartmentAccumulator());
            value.leaveRequestCount = number(row[1]);
            value.approvedLeaveRequestCount = number(row[2]);
        }
        for (Object[] row : payrollRepository.getDepartmentPayrollReport()) {
            DepartmentAccumulator value = departments.computeIfAbsent((String) row[0], ignored -> new DepartmentAccumulator());
            value.totalBaseSalary = decimal(row[1]);
            value.totalAllowances = decimal(row[2]);
            value.totalDeductions = decimal(row[3]);
            value.totalNetSalary = decimal(row[4]);
        }
        return departments.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getValue().toResponse(entry.getKey()))
                .toList();
    }

    private UUID resolveEmployeeScope(UUID requestedEmployeeId) {
        if (organizationReporter()) {
            if (requestedEmployeeId != null) requireEmployee(requestedEmployeeId);
            return requestedEmployeeId;
        }
        Employee current = currentEmployee();
        if (requestedEmployeeId != null && !requestedEmployeeId.equals(current.getId())) {
            throw new AccessDeniedException("Employees may only access their own report data");
        }
        return current.getId();
    }

    private Employee currentEmployee() {
        UUID userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));
        return employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found for authenticated user"));
    }

    private boolean organizationReporter() {
        UserPrincipal principal = SecurityUtils.getCurrentUserPrincipal()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));
        return principal.getRoleNames().contains("HR") || principal.getRoleNames().contains("ADMIN");
    }

    private void requireEmployee(UUID employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }
    }

    private static Pageable pageable(int page, int size, String sort, String direction, Map<String, String> supportedSorts) {
        if (page < 0) throw new BadRequestException("Page must be zero or greater");
        if (size < 1 || size > MAX_PAGE_SIZE) throw new BadRequestException("Page size must be between 1 and " + MAX_PAGE_SIZE);
        String property = supportedSorts.get(sort);
        if (property == null) throw new BadRequestException("Unsupported sort field: " + sort);
        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Sort direction must be 'asc' or 'desc'");
        }
        return PageRequest.of(page, size, Sort.by(sortDirection, property));
    }

    private static void validateDateRange(LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new BadRequestException("'from' date cannot be after 'to' date");
        }
    }

    private static String clean(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private static long number(Object value) {
        return value instanceof Number number ? number.longValue() : 0;
    }

    private static BigDecimal decimal(Object value) {
        if (value instanceof BigDecimal decimal) return decimal;
        if (value instanceof Number number) return BigDecimal.valueOf(number.doubleValue());
        return BigDecimal.ZERO;
    }

    private static final class DepartmentAccumulator {
        private long employeeCount;
        private long activeEmployeeCount;
        private long leaveRequestCount;
        private long approvedLeaveRequestCount;
        private BigDecimal totalBaseSalary = BigDecimal.ZERO;
        private BigDecimal totalAllowances = BigDecimal.ZERO;
        private BigDecimal totalDeductions = BigDecimal.ZERO;
        private BigDecimal totalNetSalary = BigDecimal.ZERO;

        private DepartmentReportResponse toResponse(String department) {
            return new DepartmentReportResponse(department, employeeCount, activeEmployeeCount,
                    leaveRequestCount, approvedLeaveRequestCount, totalBaseSalary, totalAllowances,
                    totalDeductions, totalNetSalary);
        }
    }
}
