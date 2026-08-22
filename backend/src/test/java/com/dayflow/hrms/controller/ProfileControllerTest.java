package com.dayflow.hrms.controller;

import com.dayflow.hrms.dto.UpdateProfileRequest;
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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileControllerTest {

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

    private User employeeUser;
    private Employee employeeProfile;
    private String employeeToken;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();

        Role employeeRole = roleRepository.findByName("EMPLOYEE").orElseThrow();

        employeeUser = userRepository.save(User.builder()
                .email("profile.employee@dayflow.com")
                .status(UserStatus.ACTIVE)
                .build());
        userRoleRepository.save(new UserRole(employeeUser, employeeRole));

        employeeProfile = employeeRepository.save(Employee.builder()
                .user(employeeUser)
                .employeeCode("EMP099")
                .firstName("Michael")
                .lastName("Scott")
                .phone("+1999999999")
                .address("Scranton, PA")
                .department("Management")
                .designation("Regional Manager")
                .joiningDate(LocalDate.of(2020, 5, 1))
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build());

        employeeToken = jwtService.generateToken(employeeUser.getId(), employeeUser.getEmail(), 3600000, Map.of("role", "authenticated"));
    }

    @Test
    @DisplayName("GET /api/v1/profile should return current authenticated employee's profile")
    void shouldGetCurrentUserProfile() throws Exception {
        mockMvc.perform(get("/api/v1/profile")
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeCode", is("EMP099")))
                .andExpect(jsonPath("$.firstName", is("Michael")))
                .andExpect(jsonPath("$.lastName", is("Scott")))
                .andExpect(jsonPath("$.email", is("profile.employee@dayflow.com")));
    }

    @Test
    @DisplayName("PUT /api/v1/profile should update permitted fields for authenticated employee")
    void shouldUpdateCurrentUserProfile() throws Exception {
        UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
                .phone("+1555555555")
                .address("1725 Slough Avenue, Scranton, PA")
                .profilePictureUrl("https://images.dayflow.com/michael.png")
                .build();

        mockMvc.perform(put("/api/v1/profile")
                        .header("Authorization", "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone", is("+1555555555")))
                .andExpect(jsonPath("$.address", is("1725 Slough Avenue, Scranton, PA")))
                .andExpect(jsonPath("$.profilePictureUrl", is("https://images.dayflow.com/michael.png")))
                // Ensure non-modifiable fields remain intact
                .andExpect(jsonPath("$.employeeCode", is("EMP099")))
                .andExpect(jsonPath("$.department", is("Management")));
    }

    @Test
    @DisplayName("GET /api/v1/profile without token should return 401 UNAUTHORIZED")
    void shouldRejectUnauthenticatedProfileRequest() throws Exception {
        mockMvc.perform(get("/api/v1/profile"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode", is("UNAUTHORIZED")));
    }
}
