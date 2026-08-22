package com.dayflow.hrms.service;

import com.dayflow.hrms.dto.*;
import com.dayflow.hrms.entity.EmploymentStatus;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for Employee and Profile management.
 */
public interface EmployeeService {

    /**
     * Retrieves the profile of the currently authenticated user.
     */
    EmployeeResponse getCurrentUserProfile();

    /**
     * Updates permitted profile fields of the currently authenticated user.
     */
    EmployeeResponse updateCurrentUserProfile(UpdateProfileRequest request);

    /**
     * Retrieves paginated and filtered list of employees (HR and ADMIN only).
     */
    PageResponse<EmployeeResponse> getEmployees(String search, String department, EmploymentStatus status, Pageable pageable);

    /**
     * Retrieves a single employee by ID (HR/ADMIN or own employee record).
     */
    EmployeeResponse getEmployeeById(UUID employeeId);

    /**
     * Creates a new employee record (HR and ADMIN only).
     */
    EmployeeResponse createEmployee(CreateEmployeeRequest request);

    /**
     * Updates an employee record (HR and ADMIN only).
     */
    EmployeeResponse updateEmployee(UUID employeeId, UpdateEmployeeRequest request);

    /**
     * Updates employment status of an employee (HR and ADMIN only).
     */
    EmployeeResponse changeEmployeeStatus(UUID employeeId, ChangeEmployeeStatusRequest request);
}
