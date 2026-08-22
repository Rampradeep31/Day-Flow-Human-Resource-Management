package com.dayflow.hrms.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * UserRole junction entity connecting User and Role.
 */
@Entity
@Table(name = "user_roles", uniqueConstraints = {
    @UniqueConstraint(name = "uq_user_roles_user_role", columnNames = {"user_id", "role_id"})
})
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_roles_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_roles_role"))
    private Role role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UserRole() {
    }

    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
    }

    public UserRole(UUID id, User user, Role role, Instant createdAt) {
        this.id = id;
        this.user = user;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRole userRole = (UserRole) o;
        return Objects.equals(user != null ? user.getId() : null, userRole.user != null ? userRole.user.getId() : null) &&
               Objects.equals(role != null ? role.getId() : null, userRole.role != null ? userRole.role.getId() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user != null ? user.getId() : null, role != null ? role.getId() : null);
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", roleId=" + (role != null ? role.getId() : null) +
                ", createdAt=" + createdAt +
                '}';
    }

    public static class Builder {
        private UUID id;
        private User user;
        private Role role;
        private Instant createdAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserRole build() {
            return new UserRole(id, user, role, createdAt);
        }
    }
}
