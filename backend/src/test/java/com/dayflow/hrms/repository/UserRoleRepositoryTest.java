package com.dayflow.hrms.repository;

import com.dayflow.hrms.entity.Role;
import com.dayflow.hrms.entity.User;
import com.dayflow.hrms.entity.UserRole;
import com.dayflow.hrms.entity.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class UserRoleRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should assign role to user and query by user and role")
    void shouldAssignRoleToUserAndQuery() {
        User user = userRepository.save(User.builder().email("admin.user@dayflow.com").status(UserStatus.ACTIVE).build());
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(adminRole)
                .build();

        UserRole savedUserRole = userRoleRepository.save(userRole);
        entityManager.flush();
        entityManager.clear();

        assertThat(savedUserRole.getId()).isNotNull();
        assertThat(savedUserRole.getCreatedAt()).isNotNull();

        List<UserRole> rolesForUser = userRoleRepository.findByUserId(user.getId());
        assertThat(rolesForUser).hasSize(1);
        assertThat(rolesForUser.get(0).getRole().getName()).isEqualTo("ADMIN");

        Optional<UserRole> found = userRoleRepository.findByUserIdAndRoleId(user.getId(), adminRole.getId());
        assertThat(found).isPresent();
        assertThat(userRoleRepository.existsByUserIdAndRoleId(user.getId(), adminRole.getId())).isTrue();
    }

    @Test
    @DisplayName("Should enforce unique constraint on (user_id, role_id) duplicate assignment")
    void shouldEnforceUniqueUserRoleConstraint() {
        User user = userRepository.save(User.builder().email("hr.user@dayflow.com").status(UserStatus.ACTIVE).build());
        Role hrRole = roleRepository.findByName("HR").orElseThrow();

        UserRole assignment1 = UserRole.builder().user(user).role(hrRole).build();
        userRoleRepository.save(assignment1);
        entityManager.flush();

        UserRole assignment2 = UserRole.builder().user(user).role(hrRole).build();
        userRoleRepository.save(assignment2);

        assertThrows(Exception.class, () -> entityManager.flush());
    }
}
