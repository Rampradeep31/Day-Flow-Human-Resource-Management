package com.dayflow.hrms.report;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRoleRepository userRoleRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private LeaveRequestRepository leaveRequestRepository;
    @Autowired private PayrollRepository payrollRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private DocumentRepository documentRepository;

    private Employee employeeA;
    private Employee employeeB;
    private String employeeAToken;
    private String hrToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        documentRepository.deleteAll();
        notificationRepository.deleteAll();
        leaveRequestRepository.deleteAll();
        payrollRepository.deleteAll();
        employeeRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();

        Role employeeRole = roleRepository.findByName("EMPLOYEE").orElseThrow();
        Role hrRole = roleRepository.findByName("HR").orElseThrow();
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();

        User userA = createUser("report-a@dayflow.com", employeeRole);
        employeeAToken = token(userA);
        employeeA = createEmployee(userA, "REP-A", "Alice", "Anderson", "Engineering",
                "Developer", LocalDate.of(2024, 1, 10), EmploymentStatus.ACTIVE);

        User userB = createUser("report-b@dayflow.com", employeeRole);
        employeeB = createEmployee(userB, "REP-B", "Bob", "Brown", "Finance",
                "Analyst", LocalDate.of(2025, 2, 20), EmploymentStatus.INACTIVE);

        User hr = createUser("report-hr@dayflow.com", hrRole);
        hrToken = token(hr);
        createEmployee(hr, "REP-HR", "Helen", "Human", "HR", "HR Manager",
                LocalDate.of(2020, 3, 1), EmploymentStatus.ACTIVE);

        User admin = createUser("report-admin@dayflow.com", adminRole);
        adminToken = token(admin);
        createEmployee(admin, "REP-AD", "Adam", "Admin", "Management", "Administrator",
                LocalDate.of(2019, 4, 1), EmploymentStatus.ACTIVE);
    }

    @Test
    @DisplayName("employee report supports filters and pagination")
    void employeeReportFiltersAndPaginates() throws Exception {
        mockMvc.perform(get("/api/v1/reports/employees")
                        .header("Authorization", bearer(hrToken))
                        .param("department", "Engineering")
                        .param("employmentStatus", "ACTIVE")
                        .param("designation", "Developer")
                        .param("from", "2024-01-01").param("to", "2024-12-31")
                        .param("page", "0").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].employeeCode", is("REP-A")))
                .andExpect(jsonPath("$.content[0].name", is("Alice Anderson")))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.pageSize", is(1)))
                .andExpect(jsonPath("$.content[0].password").doesNotExist());
    }

    @Test
    @DisplayName("employee report scopes EMPLOYEE to own row")
    void employeeReportEnforcesOwnership() throws Exception {
        mockMvc.perform(get("/api/v1/reports/employees")
                        .header("Authorization", bearer(employeeAToken))
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content[0].employeeId", is(employeeA.getId().toString())))
                .andExpect(jsonPath("$.content[0].employeeCode", is("REP-A")));
    }

    @Test
    @DisplayName("leave report supports status, type, department and date overlap filters")
    void leaveReportFilters() throws Exception {
        createLeave(employeeA, LeaveType.PAID, LeaveStatus.APPROVED,
                LocalDate.of(2026, 8, 10), LocalDate.of(2026, 8, 12));
        createLeave(employeeB, LeaveType.SICK, LeaveStatus.PENDING,
                LocalDate.of(2026, 8, 15), LocalDate.of(2026, 8, 16));

        mockMvc.perform(get("/api/v1/reports/leaves")
                        .header("Authorization", bearer(hrToken))
                        .param("leaveType", "PAID").param("status", "APPROVED")
                        .param("department", "Engineering")
                        .param("from", "2026-08-11").param("to", "2026-08-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].employeeCode", is("REP-A")))
                .andExpect(jsonPath("$.content[0].durationDays", is(3)))
                .andExpect(jsonPath("$.content[0].reason", is("Report test")));
    }

    @Test
    @DisplayName("employee cannot retrieve another employee leave report")
    void leaveReportBlocksIdor() throws Exception {
        createLeave(employeeB, LeaveType.SICK, LeaveStatus.PENDING,
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 2));

        mockMvc.perform(get("/api/v1/reports/leaves")
                        .header("Authorization", bearer(employeeAToken))
                        .param("employeeId", employeeB.getId().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("employee leave report without employeeId is securely self-filtered")
    void leaveReportDefaultsToOwner() throws Exception {
        createLeave(employeeA, LeaveType.PAID, LeaveStatus.PENDING,
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 2));
        createLeave(employeeB, LeaveType.SICK, LeaveStatus.PENDING,
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 2));

        mockMvc.perform(get("/api/v1/reports/leaves")
                        .header("Authorization", bearer(employeeAToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content[0].employeeId", is(employeeA.getId().toString())));
    }

    @Test
    @DisplayName("payroll report allows HR and ADMIN and filters by employee")
    void payrollReportAuthorizedAndFiltered() throws Exception {
        createPayroll(employeeA, "50000", "5000", "2000");
        createPayroll(employeeB, "40000", "3000", "1000");

        mockMvc.perform(get("/api/v1/reports/payroll")
                        .header("Authorization", bearer(adminToken))
                        .param("employeeId", employeeA.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content[0].employeeCode", is("REP-A")))
                .andExpect(jsonPath("$.content[0].netSalary", comparesEqualTo(53000.0)));

        mockMvc.perform(get("/api/v1/reports/payroll")
                        .header("Authorization", bearer(hrToken))
                        .param("department", "Finance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content[0].employeeCode", is("REP-B")));
    }

    @Test
    @DisplayName("payroll report denies EMPLOYEE regardless of requested employee")
    void payrollReportProtectsSalaryPrivacy() throws Exception {
        createPayroll(employeeA, "50000", "5000", "2000");
        mockMvc.perform(get("/api/v1/reports/payroll")
                        .header("Authorization", bearer(employeeAToken))
                        .param("employeeId", employeeA.getId().toString()))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/reports/payroll")
                        .header("Authorization", bearer(employeeAToken))
                        .param("employeeId", employeeB.getId().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("payroll report returns an empty page when no payroll matches")
    void payrollReportEmpty() throws Exception {
        mockMvc.perform(get("/api/v1/reports/payroll")
                        .header("Authorization", bearer(hrToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    @DisplayName("department report uses reliable database aggregates")
    void departmentReportAggregates() throws Exception {
        createLeave(employeeA, LeaveType.PAID, LeaveStatus.APPROVED,
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 2));
        createPayroll(employeeA, "50000", "5000", "2000");

        mockMvc.perform(get("/api/v1/reports/departments")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.department == 'Engineering')].employeeCount", contains(1)))
                .andExpect(jsonPath("$[?(@.department == 'Engineering')].activeEmployeeCount", contains(1)))
                .andExpect(jsonPath("$[?(@.department == 'Engineering')].approvedLeaveRequestCount", contains(1)))
                .andExpect(jsonPath("$[?(@.department == 'Engineering')].totalNetSalary", contains(comparesEqualTo(53000.0))));
    }

    @Test
    @DisplayName("department report denies EMPLOYEE")
    void departmentReportRoleSecurity() throws Exception {
        mockMvc.perform(get("/api/v1/reports/departments")
                        .header("Authorization", bearer(employeeAToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("attendance report is empty and enforces ownership without inventing data")
    void attendanceReportIsEmptyAndBlocksIdor() throws Exception {
        mockMvc.perform(get("/api/v1/reports/attendance")
                        .header("Authorization", bearer(employeeAToken))
                        .param("from", "2026-08-01").param("to", "2026-08-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));

        mockMvc.perform(get("/api/v1/reports/attendance")
                        .header("Authorization", bearer(employeeAToken))
                        .param("employeeId", employeeB.getId().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("invalid dates, UUIDs, enums, pagination and sorting return 400")
    void invalidFiltersReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/reports/leaves").header("Authorization", bearer(hrToken))
                        .param("from", "2026-09-02").param("to", "2026-09-01"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/reports/leaves").header("Authorization", bearer(hrToken))
                        .param("from", "not-a-date"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/reports/leaves").header("Authorization", bearer(hrToken))
                        .param("employeeId", "not-a-uuid"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/reports/leaves").header("Authorization", bearer(hrToken))
                        .param("status", "UNKNOWN"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/reports/employees").header("Authorization", bearer(hrToken))
                        .param("page", "-1"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/reports/employees").header("Authorization", bearer(hrToken))
                        .param("size", "101"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/reports/employees").header("Authorization", bearer(hrToken))
                        .param("sort", "password"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("report calls have no side effects")
    void reportsAreReadOnly() throws Exception {
        createLeave(employeeA, LeaveType.PAID, LeaveStatus.PENDING,
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 2));
        createPayroll(employeeA, "50000", "5000", "2000");
        long employeesBefore = employeeRepository.count();
        long leavesBefore = leaveRequestRepository.count();
        long payrollBefore = payrollRepository.count();
        long notificationsBefore = notificationRepository.count();

        mockMvc.perform(get("/api/v1/reports/departments")
                        .header("Authorization", bearer(hrToken)))
                .andExpect(status().isOk());

        org.junit.jupiter.api.Assertions.assertEquals(employeesBefore, employeeRepository.count());
        org.junit.jupiter.api.Assertions.assertEquals(leavesBefore, leaveRequestRepository.count());
        org.junit.jupiter.api.Assertions.assertEquals(payrollBefore, payrollRepository.count());
        org.junit.jupiter.api.Assertions.assertEquals(notificationsBefore, notificationRepository.count());
    }

    @Test
    @DisplayName("all report endpoints require authentication")
    void reportsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/reports/employees")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/reports/attendance")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/reports/leaves")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/reports/payroll")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/reports/departments")).andExpect(status().isUnauthorized());
    }

    private User createUser(String email, Role role) {
        User user = userRepository.save(User.builder().email(email).status(UserStatus.ACTIVE).build());
        userRoleRepository.save(new UserRole(user, role));
        return user;
    }

    private Employee createEmployee(User user, String code, String firstName, String lastName,
            String department, String designation, LocalDate joiningDate, EmploymentStatus status) {
        return employeeRepository.save(Employee.builder().user(user).employeeCode(code).firstName(firstName)
                .lastName(lastName).department(department).designation(designation).joiningDate(joiningDate)
                .employmentStatus(status).build());
    }

    private void createLeave(Employee employee, LeaveType type, LeaveStatus status, LocalDate start, LocalDate end) {
        leaveRequestRepository.save(LeaveRequest.builder().employee(employee).leaveType(type).status(status)
                .startDate(start).endDate(end).remarks("Report test").build());
    }

    private void createPayroll(Employee employee, String base, String allowances, String deductions) {
        payrollRepository.save(Payroll.builder().employee(employee).baseSalary(new BigDecimal(base))
                .allowances(new BigDecimal(allowances)).deductions(new BigDecimal(deductions)).build());
    }

    private String token(User user) {
        return jwtService.generateToken(user.getId(), user.getEmail(), 3_600_000, Map.of("role", "authenticated"));
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }
}
