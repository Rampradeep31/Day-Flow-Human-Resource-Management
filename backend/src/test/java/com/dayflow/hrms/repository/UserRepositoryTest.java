package com.dayflow.hrms.repository;

import com.dayflow.hrms.entity.User;
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
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should persist and retrieve a user with auto-generated UUID and timestamps")
    void shouldPersistAndRetrieveUser() {
        User user = User.builder()
                .email("test.user@dayflow.com")
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();

        Optional<User> found = userRepository.findById(savedUser.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test.user@dayflow.com");
        assertThat(found.get().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should find user by email and check existence")
    void shouldFindByEmailAndCheckExistence() {
        User user = User.builder()
                .email("employee@dayflow.com")
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);
        entityManager.flush();

        Optional<User> found = userRepository.findByEmail("employee@dayflow.com");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("employee@dayflow.com");

        assertThat(userRepository.existsByEmail("employee@dayflow.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@dayflow.com")).isFalse();
    }

    @Test
    @DisplayName("Should find users by status")
    void shouldFindByStatus() {
        User activeUser = User.builder().email("active@dayflow.com").status(UserStatus.ACTIVE).build();
        User inactiveUser = User.builder().email("inactive@dayflow.com").status(UserStatus.INACTIVE).build();
        userRepository.save(activeUser);
        userRepository.save(inactiveUser);
        entityManager.flush();

        List<User> activeUsers = userRepository.findByStatus(UserStatus.ACTIVE);
        assertThat(activeUsers).extracting(User::getEmail).contains("active@dayflow.com");

        List<User> inactiveUsers = userRepository.findByStatus(UserStatus.INACTIVE);
        assertThat(inactiveUsers).extracting(User::getEmail).contains("inactive@dayflow.com");
    }

    @Test
    @DisplayName("Should enforce unique constraint on user email")
    void shouldEnforceUniqueEmailConstraint() {
        User user1 = User.builder().email("duplicate@dayflow.com").status(UserStatus.ACTIVE).build();
        userRepository.save(user1);
        entityManager.flush();

        User user2 = User.builder().email("duplicate@dayflow.com").status(UserStatus.ACTIVE).build();
        userRepository.save(user2);

        assertThrows(Exception.class, () -> entityManager.flush());
    }
}
