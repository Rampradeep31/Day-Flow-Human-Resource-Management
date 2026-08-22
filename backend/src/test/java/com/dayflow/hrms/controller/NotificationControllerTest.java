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

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest {

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

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private User employeeUser1;
    private User employeeUser2;

    private Employee employee1;
    private Employee employee2;

    private String employeeToken1;
    private String employeeToken2;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        employeeRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();

        Role employeeRole = roleRepository.findByName("EMPLOYEE").orElseThrow();

        // 1. Employee 1
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

        // 2. Employee 2
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
    @DisplayName("GET /api/v1/notifications should return only authenticated employee's notifications")
    void shouldGetOwnNotifications() throws Exception {
        notificationRepository.save(Notification.builder()
                .employee(employee1)
                .type(NotificationType.LEAVE_APPROVED)
                .title("Leave Approved")
                .message("Your leave has been approved.")
                .isRead(false)
                .build());

        notificationRepository.save(Notification.builder()
                .employee(employee2)
                .type(NotificationType.PAYROLL_UPDATED)
                .title("Payroll Updated")
                .message("Salary structure updated.")
                .isRead(false)
                .build());

        mockMvc.perform(get("/api/v1/notifications")
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("Leave Approved")));
    }

    @Test
    @DisplayName("GET /api/v1/notifications/unread-count should return accurate unread count")
    void shouldGetUnreadCount() throws Exception {
        notificationRepository.save(Notification.builder()
                .employee(employee1)
                .type(NotificationType.LEAVE_APPROVED)
                .title("Leave Approved")
                .message("Approved")
                .isRead(false)
                .build());

        notificationRepository.save(Notification.builder()
                .employee(employee1)
                .type(NotificationType.PAYROLL_UPDATED)
                .title("Payroll Updated")
                .message("Updated")
                .isRead(false)
                .build());

        notificationRepository.save(Notification.builder()
                .employee(employee1)
                .type(NotificationType.ATTENDANCE_REMINDER)
                .title("Reminder")
                .message("Check-in reminder")
                .isRead(true)
                .build());

        // Employee 2 notifications should not affect employee 1 unread count
        notificationRepository.save(Notification.builder()
                .employee(employee2)
                .type(NotificationType.GENERAL)
                .title("General Announcement")
                .message("Hello")
                .isRead(false)
                .build());

        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount", is(2)));
    }

    @Test
    @DisplayName("PUT /api/v1/notifications/{id}/read should mark notification as read and set readAt")
    void shouldMarkNotificationAsRead() throws Exception {
        Notification notification = notificationRepository.save(Notification.builder()
                .employee(employee1)
                .type(NotificationType.LEAVE_APPROVED)
                .title("Leave Approved")
                .message("Approved")
                .isRead(false)
                .build());

        mockMvc.perform(put("/api/v1/notifications/" + notification.getId() + "/read")
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read", is(true)))
                .andExpect(jsonPath("$.readAt").isNotEmpty());
    }

    @Test
    @DisplayName("PUT /api/v1/notifications/{id}/read should forbid modifying another employee's notification (IDOR test)")
    void shouldForbidModifyingOtherEmployeeNotification() throws Exception {
        Notification notification = notificationRepository.save(Notification.builder()
                .employee(employee2)
                .type(NotificationType.LEAVE_APPROVED)
                .title("Jim Leave Approved")
                .message("Approved")
                .isRead(false)
                .build());

        mockMvc.perform(put("/api/v1/notifications/" + notification.getId() + "/read")
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode", is("FORBIDDEN")));
    }

    @Test
    @DisplayName("GET /api/v1/notifications without token should return 401 Unauthorized")
    void shouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isUnauthorized());
    }
}
