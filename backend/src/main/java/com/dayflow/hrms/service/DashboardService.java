package com.dayflow.hrms.service;

import com.dayflow.hrms.dto.AdminDashboardResponse;
import com.dayflow.hrms.dto.EmployeeDashboardResponse;

/**
 * Service for aggregating dashboard data from existing modules.
 */
public interface DashboardService {

    /**
     * Retrieves dashboard data for the currently authenticated employee.
     */
    EmployeeDashboardResponse getEmployeeDashboard();

    /**
     * Retrieves organization-level dashboard data for HR/Admin users.
     */
    AdminDashboardResponse getAdminDashboard();
}
