package com.dayflow.hrms.controller;

import com.dayflow.hrms.dto.CreateEmployeeRequest;
import com.dayflow.hrms.dto.CreateLeaveRequest;
import com.dayflow.hrms.dto.UpdatePayrollRequest;
import com.dayflow.hrms.entity.*;
import com.dayflow.hrms.repository.*;
import com.dayflow.hrms.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BackendHardeningTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private User hrUser;
    private User employeeUser1;
    private User employeeUser2;

    private Employee employee1;
    private Employee employee2;

    private String hrToken;
    private String employeeToken1;
    private String employeeToken2;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        leaveRequestRepository.deleteAll();
        payrollRepository.deleteAll();
        employeeRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();

        Role hrRole = roleRepository.findByName("HR").orElseThrow();
        Role empRole = roleRepository.findByName("EMPLOYEE").orElseThrow();

        // 1. HR user
        hrUser = userRepository.save(User.builder().email("hr.hardening@dayflow.com").status(UserStatus.ACTIVE).build());
        userRoleRepository.save(new UserRole(hrUser, hrRole));
        hrToken = jwtService.generateToken(hrUser.getId(), hrUser.getEmail(), 3600000, Map.of("role", "authenticated"));

        // 2. Employee 1
        employeeUser1 = userRepository.save(User.builder().email("emp1.hardening@dayflow.com").status(UserStatus.ACTIVE).build());
        userRoleRepository.save(new UserRole(employeeUser1, empRole));
        employeeToken1 = jwtService.generateToken(employeeUser1.getId(), employeeUser1.getEmail(), 3600000, Map.of("role", "authenticated"));

        employee1 = employeeRepository.save(Employee.builder()
                .user(employeeUser1)
                .employeeCode("HRD-EMP1")
                .firstName("Dwight")
                .lastName("Schrute")
                .department("Sales")
                .designation("Sales Rep")
                .joiningDate(LocalDate.of(2022, 1, 15))
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build());

        // 3. Employee 2
        employeeUser2 = userRepository.save(User.builder().email("emp2.hardening@dayflow.com").status(UserStatus.ACTIVE).build());
        userRoleRepository.save(new UserRole(employeeUser2, empRole));
        employeeToken2 = jwtService.generateToken(employeeUser2.getId(), employeeUser2.getEmail(), 3600000, Map.of("role", "authenticated"));

        employee2 = employeeRepository.save(Employee.builder()
                .user(employeeUser2)
                .employeeCode("HRD-EMP2")
                .firstName("Jim")
                .lastName("Halpert")
                .department("Sales")
                .designation("Sales Rep")
                .joiningDate(LocalDate.of(2022, 2, 1))
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build());
    }

    // ── 1. Bean Validation Hardening Tests ───────────────────────

    @Test
    @DisplayName("POST /api/v1/employees with missing required fields should return 400 with field errors")
    void shouldRejectInvalidEmployeeCreation() throws Exception {
        CreateEmployeeRequest invalidRequest = new CreateEmployeeRequest();
        // Missing firstName, lastName, employeeCode, joiningDate, and invalid email
        invalidRequest.setEmail("not-an-email");

        mockMvc.perform(post("/api/v1/employees")
                        .header("Authorization", "Bearer " + hrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("VALIDATION_FAILED")))
                .andExpect(jsonPath("$.errors.firstName", notNullValue()))
                .andExpect(jsonPath("$.errors.lastName", notNullValue()))
                .andExpect(jsonPath("$.errors.employeeCode", notNullValue()))
                .andExpect(jsonPath("$.errors.joiningDate", notNullValue()))
                .andExpect(jsonPath("$.errors.email", is("Invalid email format")));
    }

    @Test
    @DisplayName("POST /api/v1/employees with future date of birth should return 400")
    void shouldRejectFutureDateOfBirth() throws Exception {
        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .email("valid@dayflow.com")
                .employeeCode("VALID-01")
                .firstName("John")
                .lastName("Doe")
                .joiningDate(LocalDate.now())
                .dateOfBirth(LocalDate.now().plusYears(1)) // Future DOB
                .build();

        mockMvc.perform(post("/api/v1/employees")
                        .header("Authorization", "Bearer " + hrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("VALIDATION_FAILED")))
                .andExpect(jsonPath("$.errors.dateOfBirth", is("Date of birth must be in the past")));
    }

    @Test
    @DisplayName("PUT /api/v1/payroll/employee/{id} with negative salary should return 400")
    void shouldRejectNegativePayrollSalary() throws Exception {
        UpdatePayrollRequest request = UpdatePayrollRequest.builder()
                .baseSalary(new BigDecimal("-1000.00"))
                .allowances(new BigDecimal("-50.00"))
                .build();

        mockMvc.perform(put("/api/v1/payroll/employee/" + employee1.getId())
                        .header("Authorization", "Bearer " + hrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("VALIDATION_FAILED")))
                .andExpect(jsonPath("$.errors.baseSalary", is("Base salary cannot be negative")))
                .andExpect(jsonPath("$.errors.allowances", is("Allowances cannot be negative")));
    }

    @Test
    @DisplayName("POST /api/v1/leaves with startDate after endDate should return 400")
    void shouldRejectInvertedLeaveDateRange() throws Exception {
        CreateLeaveRequest request = CreateLeaveRequest.builder()
                .leaveType(LeaveType.PAID)
                .startDate(LocalDate.of(2026, 9, 10))
                .endDate(LocalDate.of(2026, 9, 5)) // Before start date
                .remarks("Vacation")
                .build();

        mockMvc.perform(post("/api/v1/leaves")
                        .header("Authorization", "Bearer " + employeeToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.message", containsString("cannot be after end date")));
    }

    // ── 2. Type Mismatch & Conversion Tests ──────────────────────

    @Test
    @DisplayName("GET /api/v1/employees/{id} with invalid UUID format should return 400 Bad Request")
    void shouldHandleInvalidUUIDTypeMismatch() throws Exception {
        mockMvc.perform(get("/api/v1/employees/not-a-valid-uuid")
                        .header("Authorization", "Bearer " + hrToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.message", containsString("Invalid value for parameter 'employeeId'")));
    }

    @Test
    @DisplayName("POST /api/v1/leaves with invalid enum string should return 400 Bad Request")
    void shouldHandleInvalidEnumInRequestBody() throws Exception {
        String invalidPayload = """
                {
                    "leaveType": "INVALID_LEAVE_TYPE",
                    "startDate": "2026-09-01",
                    "endDate": "2026-09-05"
                }
                """;

        mockMvc.perform(post("/api/v1/leaves")
                        .header("Authorization", "Bearer " + employeeToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.message", containsString("Malformed JSON")));
    }

    // ── 3. Security, RBAC & IDOR Hardening Tests ─────────────────

    @Test
    @DisplayName("Unauthenticated request should return structured 401 Unauthorized")
    void shouldReturn401ForUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("UNAUTHORIZED")));
    }

    @Test
    @DisplayName("EMPLOYEE role accessing HR-only endpoint should return structured 403 Forbidden")
    void shouldReturn403ForForbiddenRoleAccess() throws Exception {
        mockMvc.perform(get("/api/v1/employees")
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("FORBIDDEN")));
    }

    @Test
    @DisplayName("Employee accessing another employee's payroll details should return 403 Forbidden (IDOR)")
    void shouldForbidCrossUserPayrollAccess() throws Exception {
        mockMvc.perform(get("/api/v1/payroll/employee/" + employee2.getId())
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("FORBIDDEN")));
    }

    @Test
    @DisplayName("Employee marking another employee's notification read should return 403 Forbidden (IDOR)")
    void shouldForbidCrossUserNotificationModification() throws Exception {
        Notification notification = notificationRepository.save(Notification.builder()
                .employee(employee2)
                .type(NotificationType.GENERAL)
                .title("Announcement")
                .message("Jim's announcement")
                .isRead(false)
                .build());

        mockMvc.perform(put("/api/v1/notifications/" + notification.getId() + "/read")
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("FORBIDDEN")));
    }

    // ── 4. Not Found & Duplicate Resource Tests ──────────────────

    @Test
    @DisplayName("GET non-existent resource should return structured 404 Not Found")
    void shouldReturn404ForNonExistentResource() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        mockMvc.perform(get("/api/v1/employees/" + nonExistentId)
                        .header("Authorization", "Bearer " + hrToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("NOT_FOUND")));
    }

    @Test
    @DisplayName("POST duplicate employee code should return structured 409 Conflict")
    void shouldReturn409ForDuplicateEmployeeCode() throws Exception {
        CreateEmployeeRequest duplicateRequest = CreateEmployeeRequest.builder()
                .email("new.email@dayflow.com")
                .employeeCode("HRD-EMP1") // Already exists
                .firstName("Michael")
                .lastName("Scott")
                .joiningDate(LocalDate.now())
                .build();

        mockMvc.perform(post("/api/v1/employees")
                        .header("Authorization", "Bearer " + hrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("CONFLICT")))
                .andExpect(jsonPath("$.message", containsString("Employee code already exists")));
    }
}
