package com.dayflow.hrms.repository;

import com.dayflow.hrms.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class PayrollRepositoryTest {

    @Autowired
    private PayrollRepository payrollRepository;

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
        payrollRepository.deleteAll();
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
    @DisplayName("Should persist and retrieve Payroll entity with accurate net salary calculation")
    void shouldPersistAndRetrievePayroll() {
        Payroll payroll = Payroll.builder()
                .employee(testEmployee)
                .baseSalary(new BigDecimal("45000.00"))
                .allowances(new BigDecimal("5000.00"))
                .deductions(new BigDecimal("2500.00"))
                .build();

        Payroll saved = payrollRepository.save(payroll);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNetSalary()).isEqualByComparingTo("47500.00");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        Optional<Payroll> retrieved = payrollRepository.findByEmployeeIdWithDetails(testEmployee.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getEmployee().getEmployeeCode()).isEqualTo("EMP101");
        assertThat(retrieved.get().getBaseSalary()).isEqualByComparingTo("45000.00");
        assertThat(retrieved.get().getNetSalary()).isEqualByComparingTo("47500.00");
    }

    @Test
    @DisplayName("Should enforce UNIQUE constraint on employee_id in payroll table")
    void shouldEnforceUniqueEmployeePayroll() {
        payrollRepository.save(Payroll.builder()
                .employee(testEmployee)
                .baseSalary(new BigDecimal("30000.00"))
                .allowances(BigDecimal.ZERO)
                .deductions(BigDecimal.ZERO)
                .build());

        Payroll duplicate = Payroll.builder()
                .employee(testEmployee)
                .baseSalary(new BigDecimal("40000.00"))
                .allowances(BigDecimal.ZERO)
                .deductions(BigDecimal.ZERO)
                .build();

        assertThatThrownBy(() -> {
            payrollRepository.saveAndFlush(duplicate);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}
