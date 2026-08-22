package com.dayflow.hrms.controller;

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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PayrollControllerTest {

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
    private PayrollRepository payrollRepository;

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
        payrollRepository.deleteAll();
        employeeRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();

        Role hrRole = roleRepository.findByName("HR").orElseThrow();
        Role employeeRole = roleRepository.findByName("EMPLOYEE").orElseThrow();

        // 1. HR User
        hrUser = userRepository.save(User.builder().email("hr.manager@dayflow.com").status(UserStatus.ACTIVE).build());
        userRoleRepository.save(new UserRole(hrUser, hrRole));
        hrToken = jwtService.generateToken(hrUser.getId(), hrUser.getEmail(), 3600000, Map.of("role", "authenticated"));

        // 2. Employee 1
        employeeUser1 = userRepository.save(User.builder().email("emp1@dayflow.com").status(UserStatus.ACTIVE).build());
        userRoleRepository.save(new UserRole(employeeUser1, employeeRole));
        employeeToken1 = jwtService.generateToken(employeeUser1.getId(), employeeUser1.getEmail(), 3600000, Map.of("role", "authenticated"));

        employee1 = employeeRepository.save(Employee.builder()
                .user(employeeUser1)
                .employeeCode("EMP101")
                .firstName("Dwight")
                .lastName("Schrute")
                .department("Sales")
                .designation("Assistant Regional Manager")
                .joiningDate(LocalDate.of(2021, 3, 10))
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build());

        // 3. Employee 2
        employeeUser2 = userRepository.save(User.builder().email("emp2@dayflow.com").status(UserStatus.ACTIVE).build());
        userRoleRepository.save(new UserRole(employeeUser2, employeeRole));
        employeeToken2 = jwtService.generateToken(employeeUser2.getId(), employeeUser2.getEmail(), 3600000, Map.of("role", "authenticated"));

        employee2 = employeeRepository.save(Employee.builder()
                .user(employeeUser2)
                .employeeCode("EMP102")
                .firstName("Jim")
                .lastName("Halpert")
                .department("Sales")
                .designation("Sales Representative")
                .joiningDate(LocalDate.of(2021, 4, 15))
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build());
    }

    @Test
    @DisplayName("GET /api/v1/payroll/me should allow authenticated employee to view their own payroll")
    void shouldAllowEmployeeToViewOwnPayroll() throws Exception {
        payrollRepository.save(Payroll.builder()
                .employee(employee1)
                .baseSalary(new BigDecimal("60000.00"))
                .allowances(new BigDecimal("5000.00"))
                .deductions(new BigDecimal("3000.00"))
                .build());

        mockMvc.perform(get("/api/v1/payroll/me")
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeCode", is("EMP101")))
                .andExpect(jsonPath("$.baseSalary", is(60000.00)))
                .andExpect(jsonPath("$.allowances", is(5000.00)))
                .andExpect(jsonPath("$.deductions", is(3000.00)))
                .andExpect(jsonPath("$.netSalary", is(62000.00)))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/v1/payroll/me when no payroll exists should return 404 Not Found")
    void shouldReturn404WhenNoPayrollExists() throws Exception {
        mockMvc.perform(get("/api/v1/payroll/me")
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("NOT_FOUND")));
    }

    @Test
    @DisplayName("GET /api/v1/payroll/employee/{id} should allow HR to view any employee payroll")
    void shouldAllowHrToViewEmployeePayroll() throws Exception {
        payrollRepository.save(Payroll.builder()
                .employee(employee1)
                .baseSalary(new BigDecimal("50000.00"))
                .allowances(new BigDecimal("2000.00"))
                .deductions(new BigDecimal("1000.00"))
                .build());

        mockMvc.perform(get("/api/v1/payroll/employee/" + employee1.getId())
                        .header("Authorization", "Bearer " + hrToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeCode", is("EMP101")))
                .andExpect(jsonPath("$.netSalary", is(51000.00)));
    }

    @Test
    @DisplayName("GET /api/v1/payroll/employee/{id} should forbid employee from viewing another employee's payroll (IDOR test)")
    void shouldForbidEmployeeFromViewingOtherEmployeePayroll() throws Exception {
        payrollRepository.save(Payroll.builder()
                .employee(employee2)
                .baseSalary(new BigDecimal("75000.00"))
                .allowances(BigDecimal.ZERO)
                .deductions(BigDecimal.ZERO)
                .build());

        mockMvc.perform(get("/api/v1/payroll/employee/" + employee2.getId())
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode", is("FORBIDDEN")));
    }

    @Test
    @DisplayName("PUT /api/v1/payroll/employee/{id} should allow HR to update/initialize salary and calculate net salary server-side")
    void shouldAllowHrToUpdatePayroll() throws Exception {
        UpdatePayrollRequest request = UpdatePayrollRequest.builder()
                .baseSalary(new BigDecimal("80000.00"))
                .allowances(new BigDecimal("10000.00"))
                .deductions(new BigDecimal("5000.00"))
                .build();

        mockMvc.perform(put("/api/v1/payroll/employee/" + employee1.getId())
                        .header("Authorization", "Bearer " + hrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeCode", is("EMP101")))
                .andExpect(jsonPath("$.baseSalary", is(80000.00)))
                .andExpect(jsonPath("$.allowances", is(10000.00)))
                .andExpect(jsonPath("$.deductions", is(5000.00)))
                .andExpect(jsonPath("$.netSalary", is(85000.00))); // 80000 + 10000 - 5000 = 85000
    }

    @Test
    @DisplayName("PUT /api/v1/payroll/employee/{id} should forbid regular employee from modifying payroll")
    void shouldForbidEmployeeFromUpdatingPayroll() throws Exception {
        UpdatePayrollRequest request = UpdatePayrollRequest.builder()
                .baseSalary(new BigDecimal("100000.00"))
                .allowances(BigDecimal.ZERO)
                .deductions(BigDecimal.ZERO)
                .build();

        mockMvc.perform(put("/api/v1/payroll/employee/" + employee1.getId())
                        .header("Authorization", "Bearer " + employeeToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode", is("FORBIDDEN")));
    }

    @Test
    @DisplayName("PUT /api/v1/payroll/employee/{id} with negative salary values should return 400 Bad Request")
    void shouldRejectNegativeSalaryValues() throws Exception {
        UpdatePayrollRequest request = UpdatePayrollRequest.builder()
                .baseSalary(new BigDecimal("-500.00"))
                .allowances(BigDecimal.ZERO)
                .deductions(BigDecimal.ZERO)
                .build();

        mockMvc.perform(put("/api/v1/payroll/employee/" + employee1.getId())
                        .header("Authorization", "Bearer " + hrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("VALIDATION_FAILED")));
    }

    @Test
    @DisplayName("GET /api/v1/payroll/me without authentication token should return 401 Unauthorized")
    void shouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/payroll/me"))
                .andExpect(status().isUnauthorized());
    }
}
