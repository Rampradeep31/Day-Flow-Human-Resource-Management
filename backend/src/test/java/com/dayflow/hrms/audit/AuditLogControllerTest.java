package com.dayflow.hrms.audit;

import com.dayflow.hrms.audit.entity.AuditLog;
import com.dayflow.hrms.audit.enums.AuditAction;
import com.dayflow.hrms.audit.enums.AuditResourceType;
import com.dayflow.hrms.audit.enums.AuditStatus;
import com.dayflow.hrms.audit.repository.AuditLogRepository;
import com.dayflow.hrms.dto.CreateLeaveRequest;
import com.dayflow.hrms.dto.UpdateEmployeeRequest;
import com.dayflow.hrms.dto.UpdatePayrollRequest;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuditLogControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JwtService jwtService;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private DocumentRepository documentRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private LeaveRequestRepository leaveRequestRepository;
    @Autowired private PayrollRepository payrollRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private UserRoleRepository userRoleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;

    @MockBean private SupabaseStorageService storageService;

    private User employeeUser;
    private User hrUser;
    private Employee employee;
    private Employee hrEmployee;
    private String employeeToken;
    private String hrToken;
    private String adminToken;

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

        Mockito.when(storageService.uploadFile(anyString(), org.mockito.ArgumentMatchers.any(byte[].class), anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(storageService.generateSignedUrl(anyString(), anyInt()))
                .thenReturn("https://storage.example.test/temporary-url");
        Mockito.doNothing().when(storageService).deleteFile(anyString());

        Role employeeRole = roleRepository.findByName("EMPLOYEE").orElseThrow();
        Role hrRole = roleRepository.findByName("HR").orElseThrow();
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();

        employeeUser = createUser("audit-employee@dayflow.com", employeeRole);
        employeeToken = token(employeeUser);
        employee = createEmployee(employeeUser, "AUD-E", "Erin", "Employee", "Operations");

        hrUser = createUser("audit-hr@dayflow.com", hrRole);
        hrToken = token(hrUser);
        hrEmployee = createEmployee(hrUser, "AUD-HR", "Holly", "HR", "Human Resources");

        User adminUser = createUser("audit-admin@dayflow.com", adminRole);
        adminToken = token(adminUser);
        createEmployee(adminUser, "AUD-AD", "Andy", "Admin", "Management");
    }

    @Test
    @DisplayName("employee update writes immutable audit event with server actor and request metadata")
    void employeeUpdateIsAudited() throws Exception {
        UpdateEmployeeRequest request = UpdateEmployeeRequest.builder().designation("Senior Operator").build();

        mockMvc.perform(put("/api/v1/employees/" + employee.getId())
                        .header("Authorization", bearer(hrToken))
                        .header("User-Agent", "Dayflow-Audit-Test/1.0")
                        .with(http -> { http.setRemoteAddr("10.20.30.40"); return http; })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/audit-logs")
                        .header("Authorization", bearer(adminToken))
                        .param("action", "EMPLOYEE_UPDATED")
                        .param("resourceId", employee.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content[0].actorUserId", is(hrUser.getId().toString())))
                .andExpect(jsonPath("$.content[0].actorEmployeeId", is(hrEmployee.getId().toString())))
                .andExpect(jsonPath("$.content[0].actorName", is("Holly HR")))
                .andExpect(jsonPath("$.content[0].status", is("SUCCESS")))
                .andExpect(jsonPath("$.content[0].ipAddress", is("10.20.30.40")))
                .andExpect(jsonPath("$.content[0].userAgent", is("Dayflow-Audit-Test/1.0")));
    }

    @Test
    @DisplayName("leave creation and approval preserve the correct actors")
    void leaveWorkflowIsAudited() throws Exception {
        CreateLeaveRequest request = CreateLeaveRequest.builder().leaveType(LeaveType.PAID)
                .startDate(LocalDate.now().plusDays(5)).endDate(LocalDate.now().plusDays(6))
                .remarks("Personal").build();

        String response = mockMvc.perform(post("/api/v1/leaves")
                        .header("Authorization", bearer(employeeToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String leaveId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(put("/api/v1/leaves/" + leaveId + "/approve")
                        .header("Authorization", bearer(hrToken))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/audit-logs").header("Authorization", bearer(adminToken))
                        .param("resourceId", leaveId).param("sort", "createdAt").param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].action", is("LEAVE_CREATED")))
                .andExpect(jsonPath("$.content[0].actorUserId", is(employeeUser.getId().toString())))
                .andExpect(jsonPath("$.content[1].action", is("LEAVE_APPROVED")))
                .andExpect(jsonPath("$.content[1].actorUserId", is(hrUser.getId().toString())));
    }

    @Test
    @DisplayName("payroll audit distinguishes creation and update without salary leakage")
    void payrollIsAuditedWithoutSalaryValues() throws Exception {
        UpdatePayrollRequest first = UpdatePayrollRequest.builder().baseSalary(new BigDecimal("98765.43"))
                .allowances(new BigDecimal("1234.00")).deductions(new BigDecimal("321.00")).build();
        UpdatePayrollRequest second = UpdatePayrollRequest.builder().baseSalary(new BigDecimal("99999.99"))
                .allowances(BigDecimal.ZERO).deductions(BigDecimal.ZERO).build();

        performPayrollUpdate(first);
        performPayrollUpdate(second);

        mockMvc.perform(get("/api/v1/audit-logs").header("Authorization", bearer(hrToken))
                        .param("resourceType", "PAYROLL").param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].action", is("PAYROLL_CREATED")))
                .andExpect(jsonPath("$.content[1].action", is("PAYROLL_UPDATED")))
                .andExpect(jsonPath("$.content[*].description", everyItem(not(containsString("98765")))))
                .andExpect(jsonPath("$.content[*].description", everyItem(not(containsString("99999")))));
    }

    @Test
    @DisplayName("document upload and delete are audited without content, paths, or signed URLs")
    void documentLifecycleIsAuditedSafely() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "private.pdf", "application/pdf",
                "PRIVATE DOCUMENT CONTENT".getBytes());
        String response = mockMvc.perform(multipart("/api/v1/documents").file(file)
                        .param("documentType", "OTHER").header("Authorization", bearer(employeeToken)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String documentId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(delete("/api/v1/documents/" + documentId)
                        .header("Authorization", bearer(employeeToken)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/audit-logs").header("Authorization", bearer(adminToken))
                        .param("resourceId", documentId).param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].action", contains("DOCUMENT_UPLOADED", "DOCUMENT_DELETED")))
                .andExpect(jsonPath("$.content[*].description", everyItem(not(containsString("PRIVATE")))))
                .andExpect(jsonPath("$.content[*].description", everyItem(not(containsString("storage")))))
                .andExpect(jsonPath("$.content[*].description", everyItem(not(containsString("http")))));
    }

    @Test
    @DisplayName("notification is audited only on its first transition to read")
    void notificationReadIsAuditedOnce() throws Exception {
        Notification notification = notificationRepository.save(Notification.builder().employee(employee)
                .type(NotificationType.GENERAL).title("Test").message("Message").isRead(false).build());

        mockMvc.perform(put("/api/v1/notifications/" + notification.getId() + "/read")
                        .header("Authorization", bearer(employeeToken))).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/notifications/" + notification.getId() + "/read")
                        .header("Authorization", bearer(employeeToken))).andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/audit-logs").header("Authorization", bearer(hrToken))
                        .param("action", "NOTIFICATION_READ"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    @DisplayName("audit viewing role matrix enforces ADMIN/HR and denies EMPLOYEE")
    void auditRoleSecurity() throws Exception {
        mockMvc.perform(get("/api/v1/audit-logs").header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/audit-logs").header("Authorization", bearer(hrToken)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/audit-logs").header("Authorization", bearer(employeeToken)))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/audit-logs")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("audit API is append-only with no update or delete route")
    void auditApiIsImmutable() throws Exception {
        AuditLog log = saveSystemAudit(AuditAction.EMPLOYEE_UPDATED, AuditResourceType.EMPLOYEE);
        mockMvc.perform(put("/api/v1/audit-logs/" + log.getId()).header("Authorization", bearer(adminToken)))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/v1/audit-logs/" + log.getId()).header("Authorization", bearer(adminToken)))
                .andExpect(status().isNotFound());
        assertEquals(1, auditLogRepository.count());
    }

    @Test
    @DisplayName("filters, pagination, newest-first order, and empty results work")
    void filtersAndPaginationWork() throws Exception {
        for (int index = 0; index < 30; index++) {
            saveSystemAudit(index % 2 == 0 ? AuditAction.EMPLOYEE_UPDATED : AuditAction.LEAVE_CREATED,
                    index % 2 == 0 ? AuditResourceType.EMPLOYEE : AuditResourceType.LEAVE);
        }

        mockMvc.perform(get("/api/v1/audit-logs").header("Authorization", bearer(adminToken))
                        .param("action", "EMPLOYEE_UPDATED").param("status", "SUCCESS")
                        .param("page", "0").param("size", "10"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements", is(15))).andExpect(jsonPath("$.totalPages", is(2)));
        mockMvc.perform(get("/api/v1/audit-logs").header("Authorization", bearer(adminToken))
                        .param("action", "EMPLOYEE_UPDATED").param("page", "1").param("size", "10"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(5)));
        mockMvc.perform(get("/api/v1/audit-logs").header("Authorization", bearer(adminToken))
                        .param("resourceId", java.util.UUID.randomUUID().toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    @DisplayName("invalid UUID, enum, dates, pagination, and sort return 400")
    void invalidFiltersReturnBadRequest() throws Exception {
        String auth = bearer(adminToken);
        mockMvc.perform(get("/api/v1/audit-logs").header("Authorization", auth).param("actorUserId", "bad"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/audit-logs").header("Authorization", auth).param("action", "UNKNOWN"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/audit-logs").header("Authorization", auth).param("from", "bad-date"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/audit-logs").header("Authorization", auth)
                        .param("from", "2026-08-22").param("to", "2026-08-21"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/audit-logs").header("Authorization", auth).param("page", "-1"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/audit-logs").header("Authorization", auth).param("size", "101"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/audit-logs").header("Authorization", auth).param("sort", "description"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("audit GET has no side effects")
    void auditQueryHasNoSideEffects() throws Exception {
        saveSystemAudit(AuditAction.EMPLOYEE_UPDATED, AuditResourceType.EMPLOYEE);
        long auditCount = auditLogRepository.count();
        long employeeCount = employeeRepository.count();
        mockMvc.perform(get("/api/v1/audit-logs").header("Authorization", bearer(hrToken)))
                .andExpect(status().isOk());
        assertEquals(auditCount, auditLogRepository.count());
        assertEquals(employeeCount, employeeRepository.count());
    }

    private void performPayrollUpdate(UpdatePayrollRequest request) throws Exception {
        mockMvc.perform(put("/api/v1/payroll/employee/" + employee.getId())
                        .header("Authorization", bearer(hrToken)).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk());
    }

    private AuditLog saveSystemAudit(AuditAction action, AuditResourceType resourceType) {
        return auditLogRepository.save(new AuditLog(null, null, null, action, resourceType,
                java.util.UUID.randomUUID(), "System audit event", AuditStatus.SUCCESS, null, null));
    }

    private User createUser(String email, Role role) {
        User user = userRepository.save(User.builder().email(email).status(UserStatus.ACTIVE).build());
        userRoleRepository.save(new UserRole(user, role));
        return user;
    }

    private Employee createEmployee(User user, String code, String first, String last, String department) {
        return employeeRepository.save(Employee.builder().user(user).employeeCode(code).firstName(first)
                .lastName(last).department(department).designation("Test Role").joiningDate(LocalDate.of(2020, 1, 1))
                .employmentStatus(EmploymentStatus.ACTIVE).build());
    }

    private String token(User user) {
        return jwtService.generateToken(user.getId(), user.getEmail(), 3_600_000, Map.of("role", "authenticated"));
    }

    private static String bearer(String token) { return "Bearer " + token; }
}
