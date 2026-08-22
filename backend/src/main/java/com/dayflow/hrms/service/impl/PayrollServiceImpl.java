package com.dayflow.hrms.service.impl;

import com.dayflow.hrms.audit.enums.AuditAction;
import com.dayflow.hrms.audit.enums.AuditResourceType;
import com.dayflow.hrms.audit.service.AuditLogService;
import com.dayflow.hrms.dto.PayrollResponse;
import com.dayflow.hrms.dto.UpdatePayrollRequest;
import com.dayflow.hrms.entity.Employee;
import com.dayflow.hrms.entity.Payroll;
import com.dayflow.hrms.entity.NotificationType;
import com.dayflow.hrms.exception.ResourceNotFoundException;
import com.dayflow.hrms.repository.EmployeeRepository;
import com.dayflow.hrms.repository.PayrollRepository;
import com.dayflow.hrms.security.SecurityUtils;
import com.dayflow.hrms.security.UserPrincipal;
import com.dayflow.hrms.service.NotificationService;
import com.dayflow.hrms.service.PayrollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.Optional;

/**
 * Implementation of PayrollService managing salary details,
 * server-side net salary calculations, and ownership/RBAC enforcement.
 */
@Service
@Transactional
public class PayrollServiceImpl implements PayrollService {

    private static final Logger log = LoggerFactory.getLogger(PayrollServiceImpl.class);

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public PayrollServiceImpl(
            PayrollRepository payrollRepository,
            EmployeeRepository employeeRepository,
            NotificationService notificationService,
            AuditLogService auditLogService) {
        this.payrollRepository = payrollRepository;
        this.employeeRepository = employeeRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(readOnly = true)
    public PayrollResponse getMyPayroll() {
        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));

        Employee employee = employeeRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found for user: " + currentUserId));

        Payroll payroll = payrollRepository.findByEmployeeIdWithDetails(employee.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Payroll record not found for employee: " + employee.getEmployeeCode()));

        return PayrollResponse.fromEntity(payroll);
    }

    @Override
    @Transactional(readOnly = true)
    public PayrollResponse getEmployeePayroll(UUID employeeId) {
        Employee employee = employeeRepository.findByIdWithDetails(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        UserPrincipal principal = SecurityUtils.getCurrentUserPrincipal()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));

        boolean isHrOrAdmin = principal.getRoleNames().contains("HR") || principal.getRoleNames().contains("ADMIN");
        boolean isOwner = employee.getUser() != null && employee.getUser().getId().equals(principal.getId());

        if (!isHrOrAdmin && !isOwner) {
            log.warn("Access denied: User {} attempted to view payroll for employee {}",
                    principal.getEmail(), employee.getEmployeeCode());
            throw new AccessDeniedException("You do not have permission to view other employee payroll details");
        }

        Payroll payroll = payrollRepository.findByEmployeeIdWithDetails(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll record not found for employee ID: " + employeeId));

        return PayrollResponse.fromEntity(payroll);
    }

    @Override
    public PayrollResponse updateEmployeePayroll(UUID employeeId, UpdatePayrollRequest request) {
        Employee employee = employeeRepository.findByIdWithDetails(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        Optional<Payroll> existingPayroll = payrollRepository.findByEmployeeId(employeeId);
        boolean created = existingPayroll.isEmpty();
        Payroll payroll = existingPayroll
                .orElseGet(() -> Payroll.builder()
                        .employee(employee)
                        .baseSalary(BigDecimal.ZERO)
                        .allowances(BigDecimal.ZERO)
                        .deductions(BigDecimal.ZERO)
                        .netSalary(BigDecimal.ZERO)
                        .build());

        payroll.setBaseSalary(request.getBaseSalary());
        payroll.setAllowances(request.getAllowances() != null ? request.getAllowances() : BigDecimal.ZERO);
        payroll.setDeductions(request.getDeductions() != null ? request.getDeductions() : BigDecimal.ZERO);
        payroll.calculateNetSalary();

        Payroll saved = payrollRepository.save(payroll);
        log.info("Updated payroll for employee {}: Base={}, Allowances={}, Deductions={}, Net={}",
                employee.getEmployeeCode(), saved.getBaseSalary(), saved.getAllowances(), saved.getDeductions(), saved.getNetSalary());

        // Trigger PAYROLL_UPDATED notification
        try {
            notificationService.createNotification(
                    employee,
                    NotificationType.PAYROLL_UPDATED,
                    "Payroll Updated",
                    "Your payroll compensation details have been updated.",
                    "PAYROLL",
                    saved.getId()
            );
        } catch (Exception e) {
            log.warn("Failed to create notification for payroll update of employee {}: {}", employee.getEmployeeCode(), e.getMessage());
        }

        auditLogService.logSuccess(created ? AuditAction.PAYROLL_CREATED : AuditAction.PAYROLL_UPDATED,
                AuditResourceType.PAYROLL, saved.getId(), created ? "Payroll record created" : "Payroll record updated");

        return PayrollResponse.fromEntity(saved);
    }
}
