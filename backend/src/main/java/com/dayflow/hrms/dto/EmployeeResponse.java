package com.dayflow.hrms.dto;

import com.dayflow.hrms.entity.Employee;
import com.dayflow.hrms.entity.EmploymentStatus;
import com.dayflow.hrms.entity.Gender;
import com.dayflow.hrms.entity.UserStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Detailed Employee response DTO.
 */
public class EmployeeResponse {
    private UUID id;
    private UUID userId;
    private String email;
    private UserStatus userStatus;
    private Set<String> roles;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String profilePictureUrl;
    private String department;
    private String designation;
    private LocalDate joiningDate;
    private EmploymentStatus employmentStatus;
    private Instant createdAt;
    private Instant updatedAt;

    public EmployeeResponse() {
    }

    public EmployeeResponse(UUID id, UUID userId, String email, UserStatus userStatus, Set<String> roles,
                            String employeeCode, String firstName, String lastName, String fullName,
                            String phone, String address, LocalDate dateOfBirth, Gender gender,
                            String profilePictureUrl, String department, String designation,
                            LocalDate joiningDate, EmploymentStatus employmentStatus,
                            Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.userStatus = userStatus;
        this.roles = roles;
        this.employeeCode = employeeCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.profilePictureUrl = profilePictureUrl;
        this.department = department;
        this.designation = designation;
        this.joiningDate = joiningDate;
        this.employmentStatus = employmentStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static EmployeeResponse fromEntity(Employee employee) {
        if (employee == null) {
            return null;
        }

        UUID userId = null;
        String email = null;
        UserStatus userStatus = null;
        Set<String> roles = Collections.emptySet();

        if (employee.getUser() != null) {
            userId = employee.getUser().getId();
            email = employee.getUser().getEmail();
            userStatus = employee.getUser().getStatus();
            if (employee.getUser().getUserRoles() != null) {
                roles = employee.getUser().getUserRoles().stream()
                        .map(ur -> ur.getRole().getName())
                        .collect(Collectors.toSet());
            }
        }

        return EmployeeResponse.builder()
                .id(employee.getId())
                .userId(userId)
                .email(email)
                .userStatus(userStatus)
                .roles(roles)
                .employeeCode(employee.getEmployeeCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .fullName(employee.getFullName())
                .phone(employee.getPhone())
                .address(employee.getAddress())
                .dateOfBirth(employee.getDateOfBirth())
                .gender(employee.getGender())
                .profilePictureUrl(employee.getProfilePictureUrl())
                .department(employee.getDepartment())
                .designation(employee.getDesignation())
                .joiningDate(employee.getJoiningDate())
                .employmentStatus(employee.getEmploymentStatus())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public UserStatus getUserStatus() { return userStatus; }
    public void setUserStatus(UserStatus userStatus) { this.userStatus = userStatus; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }

    public EmploymentStatus getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(EmploymentStatus employmentStatus) { this.employmentStatus = employmentStatus; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID userId;
        private String email;
        private UserStatus userStatus;
        private Set<String> roles;
        private String employeeCode;
        private String firstName;
        private String lastName;
        private String fullName;
        private String phone;
        private String address;
        private LocalDate dateOfBirth;
        private Gender gender;
        private String profilePictureUrl;
        private String department;
        private String designation;
        private LocalDate joiningDate;
        private EmploymentStatus employmentStatus;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder userId(UUID userId) { this.userId = userId; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder userStatus(UserStatus userStatus) { this.userStatus = userStatus; return this; }
        public Builder roles(Set<String> roles) { this.roles = roles; return this; }
        public Builder employeeCode(String employeeCode) { this.employeeCode = employeeCode; return this; }
        public Builder firstName(String firstName) { this.firstName = firstName; return this; }
        public Builder lastName(String lastName) { this.lastName = lastName; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder dateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; return this; }
        public Builder gender(Gender gender) { this.gender = gender; return this; }
        public Builder profilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; return this; }
        public Builder department(String department) { this.department = department; return this; }
        public Builder designation(String designation) { this.designation = designation; return this; }
        public Builder joiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; return this; }
        public Builder employmentStatus(EmploymentStatus employmentStatus) { this.employmentStatus = employmentStatus; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public EmployeeResponse build() {
            return new EmployeeResponse(id, userId, email, userStatus, roles, employeeCode,
                    firstName, lastName, fullName, phone, address, dateOfBirth, gender,
                    profilePictureUrl, department, designation, joiningDate, employmentStatus,
                    createdAt, updatedAt);
        }
    }
}
