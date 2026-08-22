package com.dayflow.hrms.service;

import com.dayflow.hrms.dto.PayrollResponse;
import com.dayflow.hrms.dto.UpdatePayrollRequest;

import java.util.UUID;

/**
 * Service interface for Payroll Management operations.
 */
public interface PayrollService {

    PayrollResponse getMyPayroll();

    PayrollResponse getEmployeePayroll(UUID employeeId);

    PayrollResponse updateEmployeePayroll(UUID employeeId, UpdatePayrollRequest request);
}
