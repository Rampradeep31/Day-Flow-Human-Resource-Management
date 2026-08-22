package com.dayflow.hrms.dto;

import com.dayflow.hrms.entity.EmploymentStatus;
import com.dayflow.hrms.entity.Gender;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request payload for HR/Admin updating an employee profile.
 */
public class UpdateEmployeeRequest {

    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @Size(max = 30, message = "Phone number cannot exceed 30 characters")
    private String phone;

    private String address;
    private LocalDate dateOfBirth;
    private Gender gender;

    @Size(max = 500, message = "Profile picture URL cannot exceed 500 characters")
    private String profilePictureUrl;

    @Size(max = 100, message = "Department cannot exceed 100 characters")
    private String department;

    @Size(max = 100, message = "Designation cannot exceed 100 characters")
    private String designation;

    private LocalDate joiningDate;
    private EmploymentStatus employmentStatus;

    public UpdateEmployeeRequest() {
    }

    public UpdateEmployeeRequest(String firstName, String lastName, String phone, String address,
                                 LocalDate dateOfBirth, Gender gender, String profilePictureUrl,
                                 String department, String designation, LocalDate joiningDate,
                                 EmploymentStatus employmentStatus) {
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

        public UpdateEmployeeRequest build() {
            return new UpdateEmployeeRequest(firstName, lastName, phone, address, dateOfBirth,
                    gender, profilePictureUrl, department, designation, joiningDate, employmentStatus);
        }
    }
}
