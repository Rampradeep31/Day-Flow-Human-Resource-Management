package com.dayflow.hrms.dto;

import com.dayflow.hrms.entity.EmploymentStatus;
import com.dayflow.hrms.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for creating a new Employee profile.
 */
@Schema(description = "Payload for creating a new employee profile")
public class CreateEmployeeRequest {

    @Schema(description = "Optional user ID of an existing user account to associate with this employee")
    private UUID userId;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    @Schema(description = "Email address for user account creation if userId is not provided", example = "john.doe@dayflow.com")
    private String email;

    @NotBlank(message = "Employee code is required")
    @Size(max = 50, message = "Employee code cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-z0-9-_]+$", message = "Employee code must contain only alphanumeric characters, dashes, or underscores")
    @Schema(description = "Unique employee alphanumeric code", example = "EMP001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String employeeCode;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    @Schema(description = "Employee first name", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    @Schema(description = "Employee last name", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @Size(max = 30, message = "Phone number cannot exceed 30 characters")
    @Pattern(regexp = "^[+]?[0-9\\s-()]*$", message = "Invalid phone number format")
    @Schema(description = "Contact phone number", example = "+1-555-0199")
    private String phone;

    @Size(max = 1000, message = "Address cannot exceed 1000 characters")
    @Schema(description = "Residential address", example = "123 Main St, Scranton, PA")
    private String address;

    @Past(message = "Date of birth must be in the past")
    @Schema(description = "Date of birth (YYYY-MM-DD)", example = "1990-05-15")
    private LocalDate dateOfBirth;

    @Schema(description = "Gender (MALE, FEMALE, OTHER)", example = "MALE")
    private Gender gender;

    @Size(max = 500, message = "Profile picture URL cannot exceed 500 characters")
    @Schema(description = "Profile photo URL")
    private String profilePictureUrl;

    @Size(max = 100, message = "Department cannot exceed 100 characters")
    @Schema(description = "Assigned department", example = "Engineering")
    private String department;

    @Size(max = 100, message = "Designation cannot exceed 100 characters")
    @Schema(description = "Job title or designation", example = "Software Engineer")
    private String designation;

    @NotNull(message = "Joining date is required")
    @Schema(description = "Date employee joined the organization (YYYY-MM-DD)", example = "2023-01-15", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate joiningDate;

    @Schema(description = "Employment lifecycle status (ACTIVE, INACTIVE, PROBATION, TERMINATED, RESIGNED)", example = "ACTIVE")
    private EmploymentStatus employmentStatus;

    public CreateEmployeeRequest() {
    }

    public CreateEmployeeRequest(UUID userId, String email, String employeeCode, String firstName, String lastName,
                                 String phone, String address, LocalDate dateOfBirth, Gender gender,
                                 String profilePictureUrl, String department, String designation,
                                 LocalDate joiningDate, EmploymentStatus employmentStatus) {
        this.userId = userId;
        this.email = email;
        this.employeeCode = employeeCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.profilePictureUrl = profilePictureUrl;
        this.department = department;
        this.designation = designation;
        this.joiningDate = joiningDate;
        this.employmentStatus = employmentStatus;
    }

    // Getters and Setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID userId;
        private String email;
        private String employeeCode;
        private String firstName;
        private String lastName;
        private String phone;
        private String address;
        private LocalDate dateOfBirth;
        private Gender gender;
        private String profilePictureUrl;
        private String department;
        private String designation;
        private LocalDate joiningDate;
        private EmploymentStatus employmentStatus;

        public Builder userId(UUID userId) { this.userId = userId; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder employeeCode(String employeeCode) { this.employeeCode = employeeCode; return this; }
        public Builder firstName(String firstName) { this.firstName = firstName; return this; }
        public Builder lastName(String lastName) { this.lastName = lastName; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder dateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; return this; }
        public Builder gender(Gender gender) { this.gender = gender; return this; }
        public Builder profilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; return this; }
        public Builder department(String department) { this.department = department; return this; }
        public Builder designation(String designation) { this.designation = designation; return this; }
        public Builder joiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; return this; }
        public Builder employmentStatus(EmploymentStatus employmentStatus) { this.employmentStatus = employmentStatus; return this; }

        public CreateEmployeeRequest build() {
            return new CreateEmployeeRequest(userId, email, employeeCode, firstName, lastName,
                    phone, address, dateOfBirth, gender, profilePictureUrl, department,
                    designation, joiningDate, employmentStatus);
        }
    }
}
