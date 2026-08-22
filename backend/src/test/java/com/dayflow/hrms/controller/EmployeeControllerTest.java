package com.dayflow.hrms.controller;

import com.dayflow.hrms.dto.ChangeEmployeeStatusRequest;
import com.dayflow.hrms.dto.CreateEmployeeRequest;
import com.dayflow.hrms.dto.UpdateEmployeeRequest;
import com.dayflow.hrms.entity.*;
import com.dayflow.hrms.repository.EmployeeRepository;
import com.dayflow.hrms.repository.RoleRepository;
import com.dayflow.hrms.repository.UserRepository;
import com.dayflow.hrms.repository.UserRoleRepository;
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

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {

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
        employeeRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();

        Role hrRole = roleRepository.findByName("HR").orElseThrow();
        Role employeeRole = roleRepository.findByName("EMPLOYEE").orElseThrow();

        // 1. HR User
        hrUser = userRepository.save(User.builder().email("hr.manager@dayflow.com").status(UserStatus.ACTIVE).build());
        userRoleRepository.save(new UserRole(hrUser, hrRole));
        hrToken = jwtService.generateToken(hrUser.getId(), hrUser.getEmail(), 3600000, Map.of("role", "authenticated"));

        // 2. Employee User 1
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

        // 3. Employee User 2
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
    @DisplayName("GET /api/v1/employees should allow HR and return paginated employee list")
    void shouldAllowHrToGetEmployeesList() throws Exception {
        mockMvc.perform(get("/api/v1/employees")
                        .header("Authorization", "Bearer " + hrToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].employeeCode", hasItems("EMP101", "EMP102")));
    }

    @Test
    @DisplayName("GET /api/v1/employees should forbid standard employee (HTTP 403)")
    void shouldForbidEmployeeFromGettingEmployeesList() throws Exception {
        mockMvc.perform(get("/api/v1/employees")
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode", is("FORBIDDEN")));
    }

    @Test
    @DisplayName("POST /api/v1/employees should allow HR to create a new employee")
    void shouldAllowHrToCreateEmployee() throws Exception {
        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .email("pam.beesly@dayflow.com")
                .employeeCode("EMP103")
                .firstName("Pam")
                .lastName("Beesly")
                .department("Administration")
                .designation("Receptionist")
                .joiningDate(LocalDate.of(2022, 1, 1))
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build();

        mockMvc.perform(post("/api/v1/employees")
                        .header("Authorization", "Bearer " + hrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeCode", is("EMP103")))
                .andExpect(jsonPath("$.firstName", is("Pam")))
                .andExpect(jsonPath("$.email", is("pam.beesly@dayflow.com")));
    }

    @Test
    @DisplayName("POST /api/v1/employees with duplicate employeeCode should return 409 CONFLICT")
    void shouldRejectDuplicateEmployeeCode() throws Exception {
        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .email("new.user@dayflow.com")
                .employeeCode("EMP101") // Already taken by Dwight
                .firstName("Duplicate")
                .lastName("Code")
                .joiningDate(LocalDate.now())
                .build();

        mockMvc.perform(post("/api/v1/employees")
                        .header("Authorization", "Bearer " + hrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("CONFLICT")));
    }

    @Test
    @DisplayName("PUT /api/v1/employees/{id} should allow HR to update employee details")
    void shouldAllowHrToUpdateEmployee() throws Exception {
        UpdateEmployeeRequest updateRequest = UpdateEmployeeRequest.builder()
                .designation("Assistant TO the Regional Manager")
                .department("Sales Management")
                .build();

        mockMvc.perform(put("/api/v1/employees/" + employee1.getId())
                        .header("Authorization", "Bearer " + hrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.designation", is("Assistant TO the Regional Manager")))
                .andExpect(jsonPath("$.department", is("Sales Management")));
    }

    @Test
    @DisplayName("PATCH /api/v1/employees/{id}/status should update employment and user account status")
    void shouldAllowHrToChangeEmployeeStatus() throws Exception {
        ChangeEmployeeStatusRequest statusRequest = ChangeEmployeeStatusRequest.builder()
                .employmentStatus(EmploymentStatus.TERMINATED)
                .build();

        mockMvc.perform(patch("/api/v1/employees/" + employee1.getId() + "/status")
                        .header("Authorization", "Bearer " + hrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employmentStatus", is("TERMINATED")));

        // Verify user account status synchronized to INACTIVE
        User updatedUser = userRepository.findById(employeeUser1.getId()).orElseThrow();
        assertThat(updatedUser.getStatus()).isEqualTo(UserStatus.INACTIVE);
    }

    @Test
    @DisplayName("GET /api/v1/employees/{id} should allow employee to view own record")
    void shouldAllowEmployeeToViewOwnRecord() throws Exception {
        mockMvc.perform(get("/api/v1/employees/" + employee1.getId())
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeCode", is("EMP101")))
                .andExpect(jsonPath("$.firstName", is("Dwight")));
    }

    @Test
    @DisplayName("GET /api/v1/employees/{id} should forbid employee from viewing another employee's record")
    void shouldForbidEmployeeFromViewingOtherRecord() throws Exception {
        mockMvc.perform(get("/api/v1/employees/" + employee2.getId())
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode", is("FORBIDDEN")));
    }
}
