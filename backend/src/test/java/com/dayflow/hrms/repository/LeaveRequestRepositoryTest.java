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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LeaveRequestRepositoryTest {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

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
        leaveRequestRepository.deleteAll();
        employeeRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();

        Role employeeRole = roleRepository.findByName("EMPLOYEE").orElseThrow();

        testUser = userRepository.save(User.builder()
                .email("alex.turner@dayflow.com")
                .status(UserStatus.ACTIVE)
                .build());
        userRoleRepository.save(new UserRole(testUser, employeeRole));

        testEmployee = employeeRepository.save(Employee.builder()
                .user(testUser)
                .employeeCode("EMP001")
                .firstName("Alex")
                .lastName("Turner")
                .department("Engineering")
                .designation("Senior Software Engineer")
                .joiningDate(LocalDate.of(2022, 1, 15))
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build());
    }

    @Test
    @DisplayName("Should persist and retrieve LeaveRequest entity with generated UUID")
    void shouldPersistAndRetrieveLeaveRequest() {
        LeaveRequest leave = LeaveRequest.builder()
                .employee(testEmployee)
                .leaveType(LeaveType.PAID)
                .startDate(LocalDate.of(2026, 9, 1))
                .endDate(LocalDate.of(2026, 9, 3))
                .remarks("Annual leave trip")
                .status(LeaveStatus.PENDING)
                .build();

        LeaveRequest saved = leaveRequestRepository.save(leave);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getDurationInDays()).isEqualTo(3);

        Optional<LeaveRequest> retrieved = leaveRequestRepository.findByIdWithDetails(saved.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getEmployee().getEmployeeCode()).isEqualTo("EMP001");
        assertThat(retrieved.get().getLeaveType()).isEqualTo(LeaveType.PAID);
        assertThat(retrieved.get().getStatus()).isEqualTo(LeaveStatus.PENDING);
    }

    @Test
    @DisplayName("Should detect overlapping active leave requests")
    void shouldDetectOverlappingActiveLeaveRequests() {
        LeaveRequest existing = leaveRequestRepository.save(LeaveRequest.builder()
                .employee(testEmployee)
                .leaveType(LeaveType.PAID)
                .startDate(LocalDate.of(2026, 9, 10))
                .endDate(LocalDate.of(2026, 9, 15))
                .status(LeaveStatus.APPROVED)
                .build());

        // Overlapping request: 2026-09-12 to 2026-09-18
        List<LeaveRequest> overlaps = leaveRequestRepository.findActiveOverlappingRequests(
                testEmployee.getId(),
                LocalDate.of(2026, 9, 12),
                LocalDate.of(2026, 9, 18)
        );

        assertThat(overlaps).hasSize(1);
        assertThat(overlaps.get(0).getId()).isEqualTo(existing.getId());

        // Non-overlapping request: 2026-09-20 to 2026-09-25
        List<LeaveRequest> nonOverlaps = leaveRequestRepository.findActiveOverlappingRequests(
                testEmployee.getId(),
                LocalDate.of(2026, 9, 20),
                LocalDate.of(2026, 9, 25)
        );
        assertThat(nonOverlaps).isEmpty();
    }

    @Test
    @DisplayName("Should ignore REJECTED requests during overlap detection")
    void shouldIgnoreRejectedRequestsDuringOverlapDetection() {
        leaveRequestRepository.save(LeaveRequest.builder()
                .employee(testEmployee)
                .leaveType(LeaveType.SICK)
                .startDate(LocalDate.of(2026, 9, 10))
                .endDate(LocalDate.of(2026, 9, 15))
                .status(LeaveStatus.REJECTED)
                .build());

        List<LeaveRequest> overlaps = leaveRequestRepository.findActiveOverlappingRequests(
                testEmployee.getId(),
                LocalDate.of(2026, 9, 12),
                LocalDate.of(2026, 9, 18)
        );

        assertThat(overlaps).isEmpty();
    }

    @Test
    @DisplayName("Should find leave requests with dynamic filters and pagination")
    void shouldFindLeavesWithFilters() {
        leaveRequestRepository.save(LeaveRequest.builder()
                .employee(testEmployee)
                .leaveType(LeaveType.PAID)
                .startDate(LocalDate.of(2026, 8, 1))
                .endDate(LocalDate.of(2026, 8, 5))
                .status(LeaveStatus.APPROVED)
                .build());

        leaveRequestRepository.save(LeaveRequest.builder()
                .employee(testEmployee)
                .leaveType(LeaveType.SICK)
                .startDate(LocalDate.of(2026, 8, 10))
                .endDate(LocalDate.of(2026, 8, 12))
                .status(LeaveStatus.PENDING)
                .build());

        Page<LeaveRequest> pendingPage = leaveRequestRepository.findWithFilters(
                testEmployee.getId(),
                LeaveStatus.PENDING,
                null,
                null,
                PageRequest.of(0, 10)
        );

        assertThat(pendingPage.getTotalElements()).isEqualTo(1);
        assertThat(pendingPage.getContent().get(0).getLeaveType()).isEqualTo(LeaveType.SICK);
    }
}
