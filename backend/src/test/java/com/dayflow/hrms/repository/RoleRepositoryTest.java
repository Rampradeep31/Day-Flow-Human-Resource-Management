package com.dayflow.hrms.repository;

import com.dayflow.hrms.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should verify that default roles seeded by Flyway migration exist")
    void shouldFindDefaultSeededRoles() {
        Optional<Role> employeeRole = roleRepository.findByName("EMPLOYEE");
        assertThat(employeeRole).isPresent();
        assertThat(employeeRole.get().getName()).isEqualTo("EMPLOYEE");

        Optional<Role> hrRole = roleRepository.findByName("HR");
        assertThat(hrRole).isPresent();
        assertThat(hrRole.get().getName()).isEqualTo("HR");

        Optional<Role> adminRole = roleRepository.findByName("ADMIN");
        assertThat(adminRole).isPresent();
        assertThat(adminRole.get().getName()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Should check existence by role name")
    void shouldCheckRoleExistence() {
        assertThat(roleRepository.existsByName("EMPLOYEE")).isTrue();
        assertThat(roleRepository.existsByName("HR")).isTrue();
        assertThat(roleRepository.existsByName("ADMIN")).isTrue();
        assertThat(roleRepository.existsByName("SUPER_ADMIN")).isFalse();
    }

    @Test
    @DisplayName("Should enforce unique constraint on role name")
    void shouldEnforceUniqueRoleNameConstraint() {
        Role duplicateRole = Role.builder()
                .name("EMPLOYEE") // already seeded by Flyway
                .description("Duplicate employee role")
                .build();

        assertThrows(Exception.class, () -> roleRepository.saveAndFlush(duplicateRole));
    }
}
