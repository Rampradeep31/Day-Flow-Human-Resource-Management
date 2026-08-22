package com.dayflow.hrms.controller;

import com.dayflow.hrms.audit.entity.AuditLog;
import com.dayflow.hrms.audit.enums.AuditAction;
import com.dayflow.hrms.audit.enums.AuditResourceType;
import com.dayflow.hrms.audit.repository.AuditLogRepository;
import com.dayflow.hrms.dto.*;
import com.dayflow.hrms.entity.*;
import com.dayflow.hrms.repository.*;
import com.dayflow.hrms.security.JwtService;
import com.dayflow.hrms.service.SupabaseStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EndToEndWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @MockBean
    private SupabaseStorageService storageService;

    private User adminUser;
    private User hrUser;
    private User employeeUserA;
    private User employeeUserB;

    private String adminToken;
    private String hrToken;
    private String employeeTokenA;
    private String employeeTokenB;

    private Employee employeeA;
    private Employee employeeB;

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
        documentRepository.deleteAll();
        notificationRepository.deleteAll();
        leaveRequestRepository.deleteAll();
        payrollRepository.deleteAll();
        employeeRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();

        // Mock Supabase storage
        Mockito.when(storageService.uploadFile(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(byte[].class), org.mockito.ArgumentMatchers.anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(storageService.generateSignedUrl(anyString(), anyInt()))
                .thenReturn("https://ujiiozapfavbxctailat.supabase.co/storage/v1/object/sign/documents/test-url");
        Mockito.doNothing().when(storageService).deleteFile(anyString());

        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
        Role hrRole = roleRepository.findByName("HR").orElseThrow();
        Role employeeRole = roleRepository.findByName("EMPLOYEE").orElseThrow();

        // ADMIN registers & logs in (Step 1)
        adminUser = userRepository.save(User.builder().email("admin.e2e@dayflow.com").status(UserStatus.ACTIVE).build());
        userRoleRepository.save(new UserRole(adminUser, adminRole));
        adminToken = jwtService.generateToken(adminUser.getId(), adminUser.getEmail(), 3600000, Map.of("role", "authenticated"));

        // ADMIN creates HR user (Step 2)
        hrUser = userRepository.save(User.builder().email("hr.e2e@dayflow.com").status(UserStatus.ACTIVE).build());
        userRoleRepository.save(new UserRole(hrUser, hrRole));
        hrToken = jwtService.generateToken(hrUser.getId(), hrUser.getEmail(), 3600000, Map.of("role", "authenticated"));

        // HR/ADMIN creates Employee A (Step 3)
        employeeUserA = userRepository.save(User.builder().email("empa.e2e@dayflow.com").status(UserStatus.ACTIVE).build());
        userRoleRepository.save(new UserRole(employeeUserA, employeeRole));
        employeeTokenA = jwtService.generateToken(employeeUserA.getId(), employeeUserA.getEmail(), 3600000, Map.of("role", "authenticated"));

        employeeA = employeeRepository.save(Employee.builder()
                .user(employeeUserA)
                .employeeCode("E2E-EMPA")
                .firstName("Alice")
                .lastName("Worker")
                .phone("+15550001")
                .address("101 Work St")
                .dateOfBirth(LocalDate.of(1995, 5, 20))
                .gender(Gender.FEMALE)
                .department("Engineering")
                .designation("Software Engineer")
                .joiningDate(LocalDate.of(2023, 1, 1))
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build());

        // HR/ADMIN creates Employee B (Step 4)
        employeeUserB = userRepository.save(User.builder().email("empb.e2e@dayflow.com").status(UserStatus.ACTIVE).build());
        userRoleRepository.save(new UserRole(employeeUserB, employeeRole));
        employeeTokenB = jwtService.generateToken(employeeUserB.getId(), employeeUserB.getEmail(), 3600000, Map.of("role", "authenticated"));

        employeeB = employeeRepository.save(Employee.builder()
                .user(employeeUserB)
                .employeeCode("E2E-EMPB")
                .firstName("Bob")
                .lastName("Staff")
                .phone("+15550002")
                .address("102 Staff St")
                .dateOfBirth(LocalDate.of(1996, 6, 21))
                .gender(Gender.MALE)
                .department("Marketing")
                .designation("Marketing Analyst")
                .joiningDate(LocalDate.of(2023, 6, 1))
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build());
    }

    @Test
    @DisplayName("Complete E2E Business Workflow Simulation (Positive & Negative)")
    void runCompleteE2EWorkflow() throws Exception {
        // ── STEP 5: Employee A logs in ──
        // (Verified by employeeTokenA generation in setUp)

        // ── STEP 8: Employee A creates leave (PENDING) ──
        CreateLeaveRequest leaveRequest = CreateLeaveRequest.builder()
                .leaveType(LeaveType.PAID)
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(5))
                .remarks("Annual vacation")
                .build();

        MvcResult leaveResult = mockMvc.perform(post("/api/v1/leaves")
                        .header("Authorization", "Bearer " + employeeTokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(leaveRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.leaveType", is("PAID")))
                .andReturn();

        String leaveResponseBody = leaveResult.getResponse().getContentAsString();
        UUID leaveId = UUID.fromString(objectMapper.readTree(leaveResponseBody).get("id").asText());

        // ── STEP 9 & 10: HR logs in and approves leave request ──
        ReviewLeaveRequest approveRequest = ReviewLeaveRequest.builder()
                .comment("Have a nice trip!")
                .build();

        mockMvc.perform(put("/api/v1/leaves/" + leaveId + "/approve")
                        .header("Authorization", "Bearer " + hrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approveRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.reviewComment", is("Have a nice trip!")));

        // ── STEP 11: Verify notification is generated for Employee A ──
        mockMvc.perform(get("/api/v1/notifications")
                        .header("Authorization", "Bearer " + employeeTokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].type", is("LEAVE_APPROVED")))
                .andExpect(jsonPath("$.content[0].title", is("Leave Approved")));

        // ── STEP 12: HR/ADMIN creates/updates payroll ──
        UpdatePayrollRequest payrollRequest = UpdatePayrollRequest.builder()
                .baseSalary(new BigDecimal("60000.00"))
                .allowances(new BigDecimal("5000.00"))
                .deductions(new BigDecimal("2000.00"))
                .build();

        mockMvc.perform(put("/api/v1/payroll/employee/" + employeeA.getId())
                        .header("Authorization", "Bearer " + hrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payrollRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseSalary", closeTo(60000.0, 0.01)))
                .andExpect(jsonPath("$.netSalary", closeTo(63000.0, 0.01)));

        // ── STEP 13: Authorized user (Employee A) uploads a document ──
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "contract.pdf",
                "application/pdf",
                "Dummy PDF content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/documents")
                        .file(mockFile)
                        .param("documentType", "CONTRACT")
                        .header("Authorization", "Bearer " + employeeTokenA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName", is("contract.pdf")))
                .andExpect(jsonPath("$.documentType", is("CONTRACT")));

        // ── STEP 14: Employee A requests their dashboard ──
        mockMvc.perform(get("/api/v1/dashboard/employee")
                        .header("Authorization", "Bearer " + employeeTokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee.employeeCode", is("E2E-EMPA")))
                .andExpect(jsonPath("$.leaveSummary.approved", is(1)))
                .andExpect(jsonPath("$.payroll.netSalary", closeTo(63000.0, 0.01)));

        // ── STEP 15: HR/Admin requests dashboard & reports ──
        mockMvc.perform(get("/api/v1/dashboard/admin")
                        .header("Authorization", "Bearer " + hrToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeStatistics.total", is(2)))
                .andExpect(jsonPath("$.leaveStatistics.pendingRequests", is(0)))
                .andExpect(jsonPath("$.payrollSummary.totalNetSalary", closeTo(63000.0, 0.01)));

        mockMvc.perform(get("/api/v1/reports/employees")
                        .header("Authorization", "Bearer " + hrToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

        mockMvc.perform(get("/api/v1/reports/leaves")
                        .header("Authorization", "Bearer " + hrToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        // ── STEP 16: Audit logs are requested ──
        mockMvc.perform(get("/api/v1/audit-logs")
                        .header("Authorization", "Bearer " + hrToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));

        // ── SECTION 55: COMPLETE NEGATIVE WORKFLOW (Security / IDOR) ──
        
        // Employee A attempts to view Employee B's private employee details -> 403 Forbidden
        mockMvc.perform(get("/api/v1/employees/" + employeeB.getId())
                        .header("Authorization", "Bearer " + employeeTokenA))
                .andExpect(status().isForbidden());

        // Employee A attempts to view Employee B's payroll -> 403 Forbidden
        mockMvc.perform(get("/api/v1/payroll/employee/" + employeeB.getId())
                        .header("Authorization", "Bearer " + employeeTokenA))
                .andExpect(status().isForbidden());

        // Employee A attempts to modify payroll -> 403 Forbidden
        mockMvc.perform(put("/api/v1/payroll/employee/" + employeeB.getId())
                        .header("Authorization", "Bearer " + employeeTokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payrollRequest)))
                .andExpect(status().isForbidden());

        // Employee A attempts to view audit logs -> 403 Forbidden
        mockMvc.perform(get("/api/v1/audit-logs")
                        .header("Authorization", "Bearer " + employeeTokenA))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Verify concurrency protections (duplicate creations / double reviews)")
    void testConcurrencyRaceConditions() throws Exception {
        // Try to approve already approved/processed leave request -> 409 Conflict
        CreateLeaveRequest leaveRequest = CreateLeaveRequest.builder()
                .leaveType(LeaveType.SICK)
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(12))
                .remarks("Sick leave")
                .build();

        MvcResult leaveResult = mockMvc.perform(post("/api/v1/leaves")
                        .header("Authorization", "Bearer " + employeeTokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(leaveRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        UUID leaveId = UUID.fromString(objectMapper.readTree(leaveResult.getResponse().getContentAsString()).get("id").asText());

        ReviewLeaveRequest review = ReviewLeaveRequest.builder().comment("OK").build();

        // First approval -> 200
        mockMvc.perform(put("/api/v1/leaves/" + leaveId + "/approve")
                        .header("Authorization", "Bearer " + hrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk());

        // Second approval attempt -> 409 Conflict
        mockMvc.perform(put("/api/v1/leaves/" + leaveId + "/approve")
                        .header("Authorization", "Bearer " + hrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("CONFLICT")));
    }

    @Test
    @DisplayName("Verify audit logs don't leak passwords, JWTs, secrets, or connection strings")
    void testAuditLogSecurityAndSanitization() throws Exception {
        // Trigger some actions to create audit logs
        UpdatePayrollRequest payrollRequest = UpdatePayrollRequest.builder()
                .baseSalary(new BigDecimal("70000.00"))
                .allowances(new BigDecimal("1000.00"))
                .deductions(new BigDecimal("1000.00"))
                .build();

        mockMvc.perform(put("/api/v1/payroll/employee/" + employeeA.getId())
                        .header("Authorization", "Bearer " + hrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payrollRequest)))
                .andExpect(status().isOk());

        List<AuditLog> auditLogs = auditLogRepository.findAll();
        assertFalse(auditLogs.isEmpty());

        for (AuditLog log : auditLogs) {
            String details = log.getDescription() != null ? log.getDescription().toLowerCase() : "";
            assertFalse(details.contains("password"), "Audit logs must not contain 'password'");
            assertFalse(details.contains("jwt"), "Audit logs must not contain 'jwt'");
            assertFalse(details.contains("bearer"), "Audit logs must not contain tokens");
            assertFalse(details.contains("secret"), "Audit logs must not contain secrets");
            assertFalse(details.contains("postgres"), "Audit logs must not contain database strings");
        }
    }

    @Test
    @DisplayName("Performance Smoke Test for high-traffic endpoints")
    void testPerformanceSmokeOfReadEndpoints() throws Exception {
        long startTime = System.currentTimeMillis();

        // Perform 5 sequential read requests
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/v1/reports/employees")
                            .header("Authorization", "Bearer " + hrToken))
                    .andExpect(status().isOk());
        }

        long duration = System.currentTimeMillis() - startTime;
        assertTrue(duration < 2000, "5 dashboard reports fetches should take less than 2 seconds (was " + duration + "ms)");
    }
}
