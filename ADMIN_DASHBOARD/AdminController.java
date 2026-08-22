package com.dayflow.hrms.controller;

import com.dayflow.hrms.dto.*;
import com.dayflow.hrms.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminDashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeSummaryDTO>> getAllEmployees() {
        return ResponseEntity.ok(adminService.getAllEmployees());
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<String> updateEmployeeProfile(
            @PathVariable Long id,
            @RequestBody UpdateEmployeeDTO dto) {
        adminService.updateEmployeeDetails(id, dto);
        return ResponseEntity.ok("Employee details updated successfully");
    }

    @GetMapping("/attendance")
    public ResponseEntity<List<AttendanceRecordDTO>> getCompanyAttendance(
            @RequestParam(required = false) String date) {
        return ResponseEntity.ok(adminService.getAttendanceRecords(date));
    }

    @GetMapping("/leaves/pending")
    public ResponseEntity<List<LeaveRequestDTO>> getPendingLeaves() {
        return ResponseEntity.ok(adminService.getPendingLeaveRequests());
    }

    @PatchMapping("/leaves/{leaveId}/status")
    public ResponseEntity<String> processLeaveRequest(
            @PathVariable Long leaveId,
            @RequestBody ProcessLeaveDTO dto) {
        adminService.processLeaveRequest(leaveId, dto.getStatus(), dto.getAdminRemarks());
        return ResponseEntity.ok("Leave request processed successfully");
    }

    @PutMapping("/payroll/{employeeId}")
    public ResponseEntity<String> updateSalaryStructure(
            @PathVariable Long employeeId,
            @RequestBody SalaryStructureDTO dto) {
        adminService.updatePayroll(employeeId, dto);
        return ResponseEntity.ok("Salary structure updated successfully");
    }
}
