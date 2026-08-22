package com.dayflow.hrms.controller;

import com.dayflow.hrms.dto.CreateLeaveRequest;
import com.dayflow.hrms.dto.ReviewLeaveRequest;
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

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LeaveControllerTest {

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
        leaveRequestRepository.deleteAll();
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
                .designation("Sales Executive")
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
    @DisplayName("POST /api/v1/leaves should allow employee to apply for leave with PENDING status")
    void shouldAllowEmployeeToApplyForLeave() throws Exception {
        CreateLeaveRequest request = CreateLeaveRequest.builder()
                .leaveType(LeaveType.PAID)
                .startDate(LocalDate.of(2026, 9, 1))
                .endDate(LocalDate.of(2026, 9, 3))
                .remarks("Personal errands")
                .build();

        mockMvc.perform(post("/api/v1/leaves")
                        .header("Authorization", "Bearer " + employeeToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.employeeCode", is("EMP101")))
                .andExpect(jsonPath("$.leaveType", is("PAID")))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.durationDays", is(3)))
                .andExpect(jsonPath("$.remarks", is("Personal errands")));
    }

    @Test
    @DisplayName("POST /api/v1/leaves with start date after end date should return 400 Bad Request")
    void shouldRejectInvalidDateRange() throws Exception {
        CreateLeaveRequest request = CreateLeaveRequest.builder()
                .leaveType(LeaveType.PAID)
                .startDate(LocalDate.of(2026, 9, 10))
                .endDate(LocalDate.of(2026, 9, 5))
                .remarks("Invalid dates")
                .build();

        mockMvc.perform(post("/api/v1/leaves")
                        .header("Authorization", "Bearer " + employeeToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("BAD_REQUEST")));
    }

    @Test
    @DisplayName("POST /api/v1/leaves overlapping with active leave should return 409 Conflict")
    void shouldRejectOverlappingLeaveRequest() throws Exception {
        leaveRequestRepository.save(LeaveRequest.builder()
                .employee(employee1)
                .leaveType(LeaveType.PAID)
                .startDate(LocalDate.of(2026, 9, 10))
                .endDate(LocalDate.of(2026, 9, 15))
                .status(LeaveStatus.APPROVED)
                .build());

        CreateLeaveRequest overlappingRequest = CreateLeaveRequest.builder()
                .leaveType(LeaveType.SICK)
                .startDate(LocalDate.of(2026, 9, 12))
                .endDate(LocalDate.of(2026, 9, 16))
                .remarks("Sick during approved leave")
                .build();

        mockMvc.perform(post("/api/v1/leaves")
                        .header("Authorization", "Bearer " + employeeToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overlappingRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("CONFLICT")));
    }

    @Test
    @DisplayName("POST /api/v1/leaves overlapping with REJECTED leave should succeed (201 Created)")
    void shouldAllowLeaveOverlappingWithRejectedRequest() throws Exception {
        leaveRequestRepository.save(LeaveRequest.builder()
                .employee(employee1)
                .leaveType(LeaveType.PAID)
                .startDate(LocalDate.of(2026, 9, 10))
                .endDate(LocalDate.of(2026, 9, 15))
                .status(LeaveStatus.REJECTED)
                .build());

        CreateLeaveRequest newRequest = CreateLeaveRequest.builder()
                .leaveType(LeaveType.UNPAID)
                .startDate(LocalDate.of(2026, 9, 12))
                .endDate(LocalDate.of(2026, 9, 16))
                .remarks("Re-applying as unpaid")
                .build();

        mockMvc.perform(post("/api/v1/leaves")
                        .header("Authorization", "Bearer " + employeeToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.leaveType", is("UNPAID")));
    }

    @Test
    @DisplayName("GET /api/v1/leaves/me should return only authenticated employee's leave history")
    void shouldReturnOnlyOwnLeaves() throws Exception {
        leaveRequestRepository.save(LeaveRequest.builder()
                .employee(employee1)
                .leaveType(LeaveType.PAID)
                .startDate(LocalDate.of(2026, 8, 1))
                .endDate(LocalDate.of(2026, 8, 2))
                .status(LeaveStatus.APPROVED)
                .build());

        leaveRequestRepository.save(LeaveRequest.builder()
                .employee(employee2)
                .leaveType(LeaveType.SICK)
                .startDate(LocalDate.of(2026, 8, 5))
                .endDate(LocalDate.of(2026, 8, 6))
                .status(LeaveStatus.PENDING)
                .build());

        mockMvc.perform(get("/api/v1/leaves/me")
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content[0].employeeCode", is("EMP101")))
                .andExpect(jsonPath("$.content[0].leaveType", is("PAID")));
    }

    @Test
    @DisplayName("GET /api/v1/leaves/{id} should forbid employee from viewing another employee's leave")
    void shouldForbidEmployeeFromViewingOtherLeave() throws Exception {
        LeaveRequest emp2Leave = leaveRequestRepository.save(LeaveRequest.builder()
                .employee(employee2)
                .leaveType(LeaveType.SICK)
                .startDate(LocalDate.of(2026, 8, 5))
                .endDate(LocalDate.of(2026, 8, 6))
                .status(LeaveStatus.PENDING)
                .build());

        mockMvc.perform(get("/api/v1/leaves/" + emp2Leave.getId())
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode", is("FORBIDDEN")));
    }

    @Test
    @DisplayName("GET /api/v1/leaves should forbid regular employee from viewing all leaves")
    void shouldForbidEmployeeFromListingAllLeaves() throws Exception {
        mockMvc.perform(get("/api/v1/leaves")
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode", is("FORBIDDEN")));
    }

    @Test
    @DisplayName("GET /api/v1/leaves should allow HR to view all leave requests with filters")
    void shouldAllowHrToListAllLeaves() throws Exception {
        leaveRequestRepository.save(LeaveRequest.builder()
                .employee(employee1)
                .leaveType(LeaveType.PAID)
                .startDate(LocalDate.of(2026, 8, 1))
                .endDate(LocalDate.of(2026, 8, 2))
                .status(LeaveStatus.APPROVED)
                .build());

        leaveRequestRepository.save(LeaveRequest.builder()
                .employee(employee2)
                .leaveType(LeaveType.SICK)
                .startDate(LocalDate.of(2026, 8, 5))
                .endDate(LocalDate.of(2026, 8, 6))
                .status(LeaveStatus.PENDING)
                .build());

        mockMvc.perform(get("/api/v1/leaves?status=PENDING")
                        .header("Authorization", "Bearer " + hrToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content[0].employeeCode", is("EMP102")))
                .andExpect(jsonPath("$.content[0].status", is("PENDING")));
    }

    @Test
    @DisplayName("PUT /api/v1/leaves/{id}/approve should forbid regular employee")
    void shouldForbidEmployeeFromApprovingLeave() throws Exception {
        LeaveRequest leave = leaveRequestRepository.save(LeaveRequest.builder()
                .employee(employee1)
                .leaveType(LeaveType.PAID)
                .startDate(LocalDate.of(2026, 9, 1))
                .endDate(LocalDate.of(2026, 9, 2))
                .status(LeaveStatus.PENDING)
                .build());

        mockMvc.perform(put("/api/v1/leaves/" + leave.getId() + "/approve")
                        .header("Authorization", "Bearer " + employeeToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewLeaveRequest("Self approval"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode", is("FORBIDDEN")));
    }

    @Test
    @DisplayName("PUT /api/v1/leaves/{id}/approve should allow HR to approve pending leave")
    void shouldAllowHrToApproveLeave() throws Exception {
        LeaveRequest leave = leaveRequestRepository.save(LeaveRequest.builder()
                .employee(employee1)
                .leaveType(LeaveType.PAID)
                .startDate(LocalDate.of(2026, 9, 1))
                .endDate(LocalDate.of(2026, 9, 2))
                .status(LeaveStatus.PENDING)
                .build());

        ReviewLeaveRequest reviewRequest = ReviewLeaveRequest.builder()
                .comment("Approved by HR")
                .build();

        mockMvc.perform(put("/api/v1/leaves/" + leave.getId() + "/approve")
                        .header("Authorization", "Bearer " + hrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.reviewComment", is("Approved by HR")))
                .andExpect(jsonPath("$.reviewedByEmail", is("hr.manager@dayflow.com")))
                .andExpect(jsonPath("$.reviewedAt", notNullValue()));
    }

    @Test
    @DisplayName("PUT /api/v1/leaves/{id}/reject should allow HR to reject pending leave")
    void shouldAllowHrToRejectLeave() throws Exception {
        LeaveRequest leave = leaveRequestRepository.save(LeaveRequest.builder()
                .employee(employee1)
                .leaveType(LeaveType.SICK)
                .startDate(LocalDate.of(2026, 9, 1))
                .endDate(LocalDate.of(2026, 9, 2))
                .status(LeaveStatus.PENDING)
                .build());

        ReviewLeaveRequest reviewRequest = ReviewLeaveRequest.builder()
                .comment("Staff shortage on those dates")
                .build();

        mockMvc.perform(put("/api/v1/leaves/" + leave.getId() + "/reject")
                        .header("Authorization", "Bearer " + hrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("REJECTED")))
                .andExpect(jsonPath("$.reviewComment", is("Staff shortage on those dates")))
                .andExpect(jsonPath("$.reviewedByEmail", is("hr.manager@dayflow.com")));
    }

    @Test
    @DisplayName("PUT /api/v1/leaves/{id}/reject on already APPROVED leave should return 409 Conflict")
    void shouldRejectStatusTransitionFromApprovedToRejected() throws Exception {
        LeaveRequest leave = leaveRequestRepository.save(LeaveRequest.builder()
                .employee(employee1)
                .leaveType(LeaveType.PAID)
                .startDate(LocalDate.of(2026, 9, 1))
                .endDate(LocalDate.of(2026, 9, 2))
                .status(LeaveStatus.APPROVED)
                .build());

        mockMvc.perform(put("/api/v1/leaves/" + leave.getId() + "/reject")
                        .header("Authorization", "Bearer " + hrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewLeaveRequest("Trying to reject"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("CONFLICT")));
    }
}
