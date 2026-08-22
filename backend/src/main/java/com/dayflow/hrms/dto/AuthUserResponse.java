package com.dayflow.hrms.dto;

import com.dayflow.hrms.entity.UserStatus;

import java.util.Set;
import java.util.UUID;

/**
 * DTO representing authenticated user identity and assigned roles.
 */
public class AuthUserResponse {
    private UUID id;
    private String email;
    private UserStatus status;
    private Set<String> roles;

    public AuthUserResponse() {
    }

    public AuthUserResponse(UUID id, String email, UserStatus status, Set<String> roles) {
        this.id = id;
        this.email = email;
        this.status = status;
        this.roles = roles;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public static class Builder {
        private UUID id;
        private String email;
        private UserStatus status;
        private Set<String> roles;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder status(UserStatus status) {
            this.status = status;
            return this;
        }

        public Builder roles(Set<String> roles) {
            this.roles = roles;
            return this;
        }

        public AuthUserResponse build() {
            return new AuthUserResponse(id, email, status, roles);
        }
    }
}
