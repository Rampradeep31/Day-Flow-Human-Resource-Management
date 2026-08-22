package com.dayflow.hrms.repository;

import com.dayflow.hrms.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    private User testUser;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        employeeRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();

        Role employeeRole = roleRepository.findByName("EMPLOYEE").orElseThrow();

        testUser = userRepository.save(User.builder()
                .email("dwight.schrute@dayflow.com")
                .status(UserStatus.ACTIVE)
                .build());
        userRoleRepository.save(new UserRole(testUser, employeeRole));

        testEmployee = employeeRepository.save(Employee.builder()
                .user(testUser)
                .employeeCode("EMP101")
                .firstName("Dwight")
                .lastName("Schrute")
                .department("Sales")
                .designation("Assistant Regional Manager")
                .joiningDate(LocalDate.of(2021, 3, 10))
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build());
    }

    @Test
    @DisplayName("Should persist and retrieve Notification entity with accurate unread count")
    void shouldPersistAndRetrieveNotification() {
        Notification n1 = Notification.builder()
                .employee(testEmployee)
                .type(NotificationType.LEAVE_APPROVED)
                .title("Leave Approved")
                .message("Your leave has been approved.")
                .isRead(false)
                .build();

        Notification n2 = Notification.builder()
                .employee(testEmployee)
                .type(NotificationType.PAYROLL_UPDATED)
                .title("Payroll Updated")
                .message("Your payroll structure has been updated.")
                .isRead(true)
                .build();

        notificationRepository.save(n1);
        notificationRepository.save(n2);

        long unreadCount = notificationRepository.countByEmployeeIdAndIsReadFalse(testEmployee.getId());
        assertThat(unreadCount).isEqualTo(1);

        Page<Notification> page = notificationRepository.findByEmployeeIdWithDetails(testEmployee.getId(), PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(2);

        Optional<Notification> retrieved = notificationRepository.findByIdWithDetails(n1.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getTitle()).isEqualTo("Leave Approved");
    }
}
