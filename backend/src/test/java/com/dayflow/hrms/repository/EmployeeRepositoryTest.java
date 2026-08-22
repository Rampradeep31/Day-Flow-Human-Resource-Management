package com.dayflow.hrms.repository;

import com.dayflow.hrms.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    private User testUser;
    private Role employeeRole;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();

        employeeRole = roleRepository.findByName("EMPLOYEE").orElseThrow();

        testUser = userRepository.save(User.builder()
                .email("alex.turner@dayflow.com")
                .status(UserStatus.ACTIVE)
                .build());
        userRoleRepository.save(new UserRole(testUser, employeeRole));
    }

    @Test
    @DisplayName("Should persist and retrieve Employee entity with generated UUID")
    void shouldPersistAndRetrieveEmployee() {
        Employee employee = Employee.builder()
                .user(testUser)
                .employeeCode("EMP001")
                .firstName("Alex")
                .lastName("Turner")
                .phone("+1234567890")
                .department("Engineering")
                .designation("Senior Software Engineer")
                .joiningDate(LocalDate.of(2023, 1, 15))
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build();

        Employee saved = employeeRepository.save(employee);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getFullName()).isEqualTo("Alex Turner");

        Optional<Employee> found = employeeRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmployeeCode()).isEqualTo("EMP001");
        assertThat(found.get().getDepartment()).isEqualTo("Engineering");
    }

    @Test
    @DisplayName("Should find employee by user ID and employee code")
    void shouldFindByUserIdAndEmployeeCode() {
        Employee employee = employeeRepository.save(Employee.builder()
                .user(testUser)
                .employeeCode("EMP002")
                .firstName("Sarah")
                .lastName("Connor")
                .joiningDate(LocalDate.now())
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build());

        Optional<Employee> byUserId = employeeRepository.findByUserId(testUser.getId());
        assertThat(byUserId).isPresent();
        assertThat(byUserId.get().getEmployeeCode()).isEqualTo("EMP002");

        Optional<Employee> byCode = employeeRepository.findByEmployeeCode("EMP002");
        assertThat(byCode).isPresent();
        assertThat(byCode.get().getFirstName()).isEqualTo("Sarah");

        assertThat(employeeRepository.existsByEmployeeCode("EMP002")).isTrue();
        assertThat(employeeRepository.existsByEmployeeCode("EMP999")).isFalse();
    }

    @Test
    @DisplayName("Should filter and paginate employees correctly")
    void shouldFilterAndPaginateEmployees() {
        employeeRepository.save(Employee.builder()
                .user(testUser)
                .employeeCode("EMP003")
                .firstName("John")
                .lastName("Doe")
                .department("HR")
                .joiningDate(LocalDate.now())
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build());

        User user2 = userRepository.save(User.builder()
                .email("jane.smith@dayflow.com")
                .status(UserStatus.ACTIVE)
                .build());
        userRoleRepository.save(new UserRole(user2, employeeRole));

        employeeRepository.save(Employee.builder()
                .user(user2)
                .employeeCode("EMP004")
                .firstName("Jane")
                .lastName("Smith")
                .department("Engineering")
                .joiningDate(LocalDate.now())
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build());

        Page<Employee> engPage = employeeRepository.findWithFilters(null, "Engineering", EmploymentStatus.ACTIVE, PageRequest.of(0, 10));
        assertThat(engPage.getTotalElements()).isEqualTo(1);
        assertThat(engPage.getContent().get(0).getEmployeeCode()).isEqualTo("EMP004");

        Page<Employee> searchPage = employeeRepository.findWithFilters("smith", null, null, PageRequest.of(0, 10));
        assertThat(searchPage.getTotalElements()).isEqualTo(1);
        assertThat(searchPage.getContent().get(0).getLastName()).isEqualTo("Smith");
    }

    @Test
    @DisplayName("Should enforce unique constraint on employeeCode")
    void shouldEnforceUniqueEmployeeCode() {
        employeeRepository.save(Employee.builder()
                .user(testUser)
                .employeeCode("EMP_DUP")
                .firstName("First")
                .lastName("User")
                .joiningDate(LocalDate.now())
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build());

        User user2 = userRepository.save(User.builder()
                .email("user2@dayflow.com")
                .status(UserStatus.ACTIVE)
                .build());

        Employee duplicateCodeEmployee = Employee.builder()
                .user(user2)
                .employeeCode("EMP_DUP")
                .firstName("Second")
                .lastName("User")
                .joiningDate(LocalDate.now())
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build();

        assertThatThrownBy(() -> employeeRepository.saveAndFlush(duplicateCodeEmployee))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Should enforce unique constraint on user_id (one employee per user)")
    void shouldEnforceOneEmployeePerUser() {
        employeeRepository.save(Employee.builder()
                .user(testUser)
                .employeeCode("EMP_001")
                .firstName("First")
                .lastName("Record")
                .joiningDate(LocalDate.now())
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build());

        Employee duplicateUserEmployee = Employee.builder()
                .user(testUser)
                .employeeCode("EMP_002")
                .firstName("Second")
                .lastName("Record")
                .joiningDate(LocalDate.now())
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build();

        assertThatThrownBy(() -> employeeRepository.saveAndFlush(duplicateUserEmployee))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
