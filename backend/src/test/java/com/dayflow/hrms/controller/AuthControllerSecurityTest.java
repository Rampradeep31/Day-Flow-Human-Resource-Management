package com.dayflow.hrms.controller;

import com.dayflow.hrms.entity.Role;
import com.dayflow.hrms.entity.User;
import com.dayflow.hrms.entity.UserRole;
import com.dayflow.hrms.entity.UserStatus;
import com.dayflow.hrms.repository.RoleRepository;
import com.dayflow.hrms.repository.UserRepository;
import com.dayflow.hrms.repository.UserRoleRepository;
import com.dayflow.hrms.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    private User employeeUser;
    private User hrUser;
    private User adminUser;
    private User inactiveUser;

    private String employeeToken;
    private String hrToken;
    private String adminToken;
    private String inactiveToken;

    @BeforeEach
    void setUp() {
        userRoleRepository.deleteAll();
        userRepository.deleteAll();

        Role employeeRole = roleRepository.findByName("EMPLOYEE").orElseThrow();
        Role hrRole = roleRepository.findByName("HR").orElseThrow();
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();

        // 1. Create Employee User
        employeeUser = userRepository.save(User.builder()
                .email("employee.security@dayflow.com")
                .status(UserStatus.ACTIVE)
                .build());
        userRoleRepository.save(new UserRole(employeeUser, employeeRole));
        employeeUser = userRepository.findById(employeeUser.getId()).orElseThrow();
        employeeToken = jwtService.generateToken(employeeUser.getId(), employeeUser.getEmail(), 3600000, Map.of("role", "authenticated"));

        // 2. Create HR User
        hrUser = userRepository.save(User.builder()
                .email("hr.security@dayflow.com")
                .status(UserStatus.ACTIVE)
                .build());
        userRoleRepository.save(new UserRole(hrUser, hrRole));
        hrUser = userRepository.findById(hrUser.getId()).orElseThrow();
        hrToken = jwtService.generateToken(hrUser.getId(), hrUser.getEmail(), 3600000, Map.of("role", "authenticated"));

        // 3. Create Admin User
        adminUser = userRepository.save(User.builder()
                .email("admin.security@dayflow.com")
                .status(UserStatus.ACTIVE)
                .build());
        userRoleRepository.save(new UserRole(adminUser, adminRole));
        adminUser = userRepository.findById(adminUser.getId()).orElseThrow();
        adminToken = jwtService.generateToken(adminUser.getId(), adminUser.getEmail(), 3600000, Map.of("role", "authenticated"));

        // 4. Create Inactive User
        inactiveUser = userRepository.save(User.builder()
                .email("inactive.security@dayflow.com")
                .status(UserStatus.INACTIVE)
                .build());
        userRoleRepository.save(new UserRole(inactiveUser, employeeRole));
        inactiveToken = jwtService.generateToken(inactiveUser.getId(), inactiveUser.getEmail(), 3600000, Map.of("role", "authenticated"));
    }

    @Test
    @DisplayName("Public endpoint /api/v1/health should be accessible without token")
    void shouldAllowPublicAccessToHealth() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.service", is("dayflow-backend")));
    }

    @Test
    @DisplayName("Protected endpoint /api/v1/auth/me should reject missing token with 401 UNAUTHORIZED")
    void shouldRejectMissingToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("UNAUTHORIZED")));
    }

    @Test
    @DisplayName("Protected endpoint /api/v1/auth/me should reject invalid token with 401 UNAUTHORIZED")
    void shouldRejectInvalidToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer invalid-tampered-token-12345"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("UNAUTHORIZED")));
    }

    @Test
    @DisplayName("Protected endpoint should reject inactive user token with 401 UNAUTHORIZED")
    void shouldRejectInactiveUser() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + inactiveToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("UNAUTHORIZED")));
    }

    @Test
    @DisplayName("Valid token should authenticate user and return identity and roles on /api/v1/auth/me")
    void shouldAuthenticateValidUser() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", is("employee.security@dayflow.com")))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.roles", hasItem("EMPLOYEE")));
    }

    @Test
    @DisplayName("Employee role should access employee-only endpoint")
    void shouldAllowEmployeeAccessToEmployeeEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/auth/employee-only")
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Employee role accessing admin-only endpoint should be forbidden (HTTP 403)")
    void shouldForbidEmployeeFromAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/auth/admin-only")
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("FORBIDDEN")));
    }

    @Test
    @DisplayName("HR role should access hr-only endpoint")
    void shouldAllowHrAccessToHrEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/auth/hr-only")
                        .header("Authorization", "Bearer " + hrToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Admin role should access admin-only endpoint")
    void shouldAllowAdminAccessToAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/auth/admin-only")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}
