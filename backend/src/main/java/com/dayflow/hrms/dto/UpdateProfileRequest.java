package com.dayflow.hrms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request payload for an employee updating their own personal profile fields.
 */
@Schema(description = "Payload for an employee updating their personal contact and profile details")
public class UpdateProfileRequest {

    @Size(max = 30, message = "Phone number cannot exceed 30 characters")
    @Pattern(regexp = "^[+]?[0-9\\s-()]*$", message = "Invalid phone number format")
    @Schema(description = "Personal contact phone number", example = "+1-555-0188")
    private String phone;

    @Size(max = 1000, message = "Address cannot exceed 1000 characters")
    @Schema(description = "Residential address", example = "789 Oak Ave, Scranton, PA")
    private String address;

    @Size(max = 500, message = "Profile picture URL cannot exceed 500 characters")
    @Schema(description = "Profile photo URL")
    private String profilePictureUrl;

    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String phone, String address, String profilePictureUrl) {
        this.phone = phone;
        this.address = address;
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String phone;
        private String address;
        private String profilePictureUrl;

        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder profilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; return this; }

        public UpdateProfileRequest build() {
            return new UpdateProfileRequest(phone, address, profilePictureUrl);
        }
    }
}
