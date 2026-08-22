package com.dayflow.hrms.controller;

import com.dayflow.hrms.dto.EmployeeResponse;
import com.dayflow.hrms.dto.UpdateProfileRequest;
import com.dayflow.hrms.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Employee personal profile management.
 */
@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "Profile", description = "Personal employee profile operations")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private final EmployeeService employeeService;

    public ProfileController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'ADMIN')")
    @Operation(summary = "Get current employee profile", description = "Retrieves the profile of the authenticated user")
    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Profile not found")
    public ResponseEntity<EmployeeResponse> getProfile() {
        return ResponseEntity.ok(employeeService.getCurrentUserProfile());
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'ADMIN')")
    @Operation(summary = "Update current employee profile", description = "Updates permitted fields (phone, address, profile picture) of the authenticated user")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request payload")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Profile not found")
    public ResponseEntity<EmployeeResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(employeeService.updateCurrentUserProfile(request));
    }
}
