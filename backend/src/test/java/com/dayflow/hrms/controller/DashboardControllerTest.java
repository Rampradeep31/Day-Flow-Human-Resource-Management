package com.dayflow.hrms.controller;

import com.dayflow.hrms.entity.*;
import com.dayflow.hrms.repository.*;
import com.dayflow.hrms.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRoleRepository userRoleRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private LeaveRequestRepository leaveRequestRepository;
    @Autowired private PayrollRepository payrollRepository;
    @Autowired private NotificationRepository notificationRepository;

    private String employeeToken;
    private String hrToken;
    private String adminToken;
    private String employee2Token;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        leaveRequestRepository.deleteAll();
        payrollRepository.deleteAll();
        employeeRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();

        Role empRole = roleRepository.findByName("EMPLOYEE").orElseThrow();
        Role hrRole = roleRepository.findByName("HR").orElseThrow();
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();

        // Employee 1
        User user1 = userRepository.save(User.builder().email("dash-emp1@dayflow.com").status(UserStatus.ACTIVE).build());
        userRoleRepository.save(new UserRole(user1, empRole));
        employeeToken = jwtService.generateToken(user1.getId(), user1.getEmail(), 3600000, Map.of("role", "authenticated"));
        employee1 = employeeRepository.save(Employee.builder()
                .user(user1).employeeCode("DASH-E1").firstName("Jim").lastName("Halpert")
                .department("Sales").designation("Sales Rep").joiningDate(LocalDate.of(2022, 1, 10))
                .employmentStatus(EmploymentStatus.ACTIVE).build());

        // Employee 2
        User user2 = userRepository.save(User.builder().email("dash-emp2@dayflow.com").status(UserStatus.ACTIVE).build());
        userRoleRepository.save(new UserRole(user2, empRole));
        employee2Token = jwtService.generateToken(user2.getId(), user2.getEmail(), 3600000, Map.of("role", "authenticated"));
        employee2 = employeeRepository.save(Employee.builder()
                .user(user2).employeeCode("DASH-E2").firstName("Pam").lastName("Beesly")
                .department("Reception").designation("Receptionist").joiningDate(LocalDate.of(2022, 2, 15))
                .employmentStatus(EmploymentStatus.ACTIVE).build());

        // HR user
        User hrUser = userRepository.save(User.builder().email("dash-hr@dayflow.com").status(UserStatus.ACTIVE).build());
        userRoleRepository.save(new UserRole(hrUser, hrRole));
        hrToken = jwtService.generateToken(hrUser.getId(), hrUser.getEmail(), 3600000, Map.of("role", "authenticated"));
        employeeRepository.save(Employee.builder()
                .user(hrUser).employeeCode("DASH-HR").firstName("Toby").lastName("Flenderson")
                .department("HR").designation("HR Manager").joiningDate(LocalDate.of(2021, 5, 1))
                .employmentStatus(EmploymentStatus.ACTIVE).build());

        // Admin user
        User adminUser = userRepository.save(User.builder().email("dash-admin@dayflow.com").status(UserStatus.ACTIVE).build());
        userRoleRepository.save(new UserRole(adminUser, adminRole));
        adminToken = jwtService.generateToken(adminUser.getId(), adminUser.getEmail(), 3600000, Map.of("role", "authenticated"));
        employeeRepository.save(Employee.builder()
                .user(adminUser).employeeCode("DASH-AD").firstName("Michael").lastName("Scott")
                .department("Management").designation("Regional Manager").joiningDate(LocalDate.of(2020, 3, 1))
                .employmentStatus(EmploymentStatus.ACTIVE).build());
    }

    // ── Employee Dashboard Tests ────────────────────────────────

    @Test
    @DisplayName("GET /api/v1/dashboard/employee should return full dashboard with data")
    void shouldReturnEmployeeDashboardWithData() throws Exception {
        // Create leave requests for employee 1
        leaveRequestRepository.save(LeaveRequest.builder()
                .employee(employee1).leaveType(LeaveType.PAID)
                .startDate(LocalDate.now().plusDays(5)).endDate(LocalDate.now().plusDays(7))
                .status(LeaveStatus.PENDING).build());
        leaveRequestRepository.save(LeaveRequest.builder()
                .employee(employee1).leaveType(LeaveType.SICK)
                .startDate(LocalDate.now().minusDays(10)).endDate(LocalDate.now().minusDays(9))
                .status(LeaveStatus.APPROVED).build());
        leaveRequestRepository.save(LeaveRequest.builder()
                .employee(employee1).leaveType(LeaveType.UNPAID)
                .startDate(LocalDate.now().minusDays(20)).endDate(LocalDate.now().minusDays(18))
                .status(LeaveStatus.REJECTED).build());

        // Create payroll for employee 1
        payrollRepository.save(Payroll.builder()
                .employee(employee1)
                .baseSalary(new BigDecimal("50000"))
                .allowances(new BigDecimal("10000"))
                .deductions(new BigDecimal("5000"))
                .build());

        // Create notifications for employee 1
        notificationRepository.save(Notification.builder()
                .employee(employee1).type(NotificationType.LEAVE_APPROVED)
                .title("Leave Approved").message("Your leave was approved.").isRead(false).build());
        notificationRepository.save(Notification.builder()
                .employee(employee1).type(NotificationType.PAYROLL_UPDATED)
                .title("Payroll Updated").message("Your salary was updated.").isRead(true).build());

        mockMvc.perform(get("/api/v1/dashboard/employee")
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee.employeeCode", is("DASH-E1")))
                .andExpect(jsonPath("$.employee.name", is("Jim Halpert")))
                .andExpect(jsonPath("$.employee.department", is("Sales")))
                .andExpect(jsonPath("$.employee.email", is("dash-emp1@dayflow.com")))
                .andExpect(jsonPath("$.attendance.status", is("NOT_AVAILABLE")))
                .andExpect(jsonPath("$.leaveSummary.totalRequests", is(3)))
                .andExpect(jsonPath("$.leaveSummary.pending", is(1)))
                .andExpect(jsonPath("$.leaveSummary.approved", is(1)))
                .andExpect(jsonPath("$.leaveSummary.rejected", is(1)))
                .andExpect(jsonPath("$.recentNotifications", hasSize(2)))
                .andExpect(jsonPath("$.unreadNotificationsCount", is(1)))
                .andExpect(jsonPath("$.payroll.baseSalary", closeTo(50000.0, 0.01)))
                .andExpect(jsonPath("$.payroll.netSalary", closeTo(55000.0, 0.01)));
    }

    @Test
    @DisplayName("GET /api/v1/dashboard/employee should return successfully with empty data")
    void shouldReturnEmployeeDashboardWithEmptyData() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/employee")
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee.employeeCode", is("DASH-E1")))
                .andExpect(jsonPath("$.attendance.status", is("NOT_AVAILABLE")))
                .andExpect(jsonPath("$.leaveSummary.totalRequests", is(0)))
                .andExpect(jsonPath("$.leaveSummary.pending", is(0)))
                .andExpect(jsonPath("$.recentNotifications", hasSize(0)))
                .andExpect(jsonPath("$.unreadNotificationsCount", is(0)))
                .andExpect(jsonPath("$.payroll").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/v1/dashboard/employee should isolate data by employee ownership")
    void shouldIsolateEmployeeDashboardData() throws Exception {
        // Data for employee 2 only
        leaveRequestRepository.save(LeaveRequest.builder()
                .employee(employee2).leaveType(LeaveType.PAID)
                .startDate(LocalDate.now().plusDays(1)).endDate(LocalDate.now().plusDays(3))
                .status(LeaveStatus.PENDING).build());
        notificationRepository.save(Notification.builder()
                .employee(employee2).type(NotificationType.LEAVE_APPROVED)
                .title("Pam Leave").message("Pam's leave.").isRead(false).build());
        payrollRepository.save(Payroll.builder()
                .employee(employee2).baseSalary(new BigDecimal("40000"))
                .allowances(BigDecimal.ZERO).deductions(BigDecimal.ZERO).build());

        // Employee 1 dashboard should see none of employee 2's data
        mockMvc.perform(get("/api/v1/dashboard/employee")
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee.employeeCode", is("DASH-E1")))
                .andExpect(jsonPath("$.leaveSummary.totalRequests", is(0)))
                .andExpect(jsonPath("$.recentNotifications", hasSize(0)))
                .andExpect(jsonPath("$.payroll").doesNotExist());
    }

    // ── Admin Dashboard Tests ───────────────────────────────────

    @Test
    @DisplayName("GET /api/v1/dashboard/admin should return admin dashboard with statistics")
    void shouldReturnAdminDashboard() throws Exception {
        // Create a leave request
        leaveRequestRepository.save(LeaveRequest.builder()
                .employee(employee1).leaveType(LeaveType.PAID)
                .startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(2))
                .status(LeaveStatus.APPROVED).build());
        leaveRequestRepository.save(LeaveRequest.builder()
                .employee(employee2).leaveType(LeaveType.SICK)
                .startDate(LocalDate.now().plusDays(5)).endDate(LocalDate.now().plusDays(6))
                .status(LeaveStatus.PENDING).build());

        // Create payroll records
        payrollRepository.save(Payroll.builder()
                .employee(employee1).baseSalary(new BigDecimal("50000"))
                .allowances(new BigDecimal("10000")).deductions(new BigDecimal("5000")).build());
        payrollRepository.save(Payroll.builder()
                .employee(employee2).baseSalary(new BigDecimal("40000"))
                .allowances(new BigDecimal("8000")).deductions(new BigDecimal("3000")).build());

        mockMvc.perform(get("/api/v1/dashboard/admin")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeStatistics.total", is(4)))
                .andExpect(jsonPath("$.employeeStatistics.active", is(4)))
                .andExpect(jsonPath("$.attendanceStatistics.presentToday", is(0)))
                .andExpect(jsonPath("$.leaveStatistics.onLeaveToday", is(1)))
                .andExpect(jsonPath("$.leaveStatistics.pendingRequests", is(1)))
                .andExpect(jsonPath("$.departmentStatistics", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$.recentActivities", hasSize(2)))
                .andExpect(jsonPath("$.payrollSummary.totalBaseSalary", closeTo(90000.0, 0.01)))
                .andExpect(jsonPath("$.payrollSummary.totalNetSalary", closeTo(100000.0, 0.01)));
    }

    @Test
    @DisplayName("GET /api/v1/dashboard/admin should be accessible by HR role")
    void shouldAllowHrAccessToAdminDashboard() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/admin")
                        .header("Authorization", "Bearer " + hrToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeStatistics.total", is(4)));
    }

    @Test
    @DisplayName("GET /api/v1/dashboard/admin should return 403 for EMPLOYEE role")
    void shouldForbidEmployeeFromAdminDashboard() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/admin")
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isForbidden());
    }

    // ── Security Tests ──────────────────────────────────────────

    @Test
    @DisplayName("GET /api/v1/dashboard/employee without token should return 401")
    void shouldReturn401ForUnauthenticatedEmployeeDashboard() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/employee"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/dashboard/admin without token should return 401")
    void shouldReturn401ForUnauthenticatedAdminDashboard() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/admin"))
                .andExpect(status().isUnauthorized());
    }
}
