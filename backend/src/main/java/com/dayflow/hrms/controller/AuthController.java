package com.dayflow.hrms.controller;

import com.dayflow.hrms.dto.AuthUserResponse;
import com.dayflow.hrms.security.SecurityUtils;
import com.dayflow.hrms.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST Controller exposing identity and role authorization endpoints for verification.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication and authorization verification endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AuthController {

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user identity", description = "Returns details and roles of the authenticated Supabase user")
    @ApiResponse(responseCode = "200", description = "Authenticated user retrieved")
    @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token")
    public ResponseEntity<AuthUserResponse> getCurrentUser() {
        return SecurityUtils.getCurrentUserPrincipal()
                .map(principal -> ResponseEntity.ok(
                        AuthUserResponse.builder()
                                .id(principal.getId())
                                .email(principal.getEmail())
                                .status(principal.getStatus())
                                .roles(principal.getRoleNames())
                                .build()
                ))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin restricted endpoint", description = "Accessible only to users with ADMIN role")
    @ApiResponse(responseCode = "200", description = "Access granted to Admin")
    @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role")
    public ResponseEntity<Map<String, String>> adminOnly() {
        return ResponseEntity.ok(Map.of("message", "Welcome Admin, authorized access granted."));
    }

    @GetMapping("/hr-only")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @Operation(summary = "HR restricted endpoint", description = "Accessible only to users with HR or ADMIN role")
    @ApiResponse(responseCode = "200", description = "Access granted to HR/Admin")
    @ApiResponse(responseCode = "403", description = "Forbidden - requires HR or ADMIN role")
    public ResponseEntity<Map<String, String>> hrOnly() {
        return ResponseEntity.ok(Map.of("message", "Welcome HR/Admin, authorized access granted."));
    }

    @GetMapping("/employee-only")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'ADMIN')")
    @Operation(summary = "Employee endpoint", description = "Accessible to EMPLOYEE, HR, and ADMIN roles")
    @ApiResponse(responseCode = "200", description = "Access granted to Employee")
    @ApiResponse(responseCode = "403", description = "Forbidden - requires valid role")
    public ResponseEntity<Map<String, String>> employeeOnly() {
        return ResponseEntity.ok(Map.of("message", "Welcome Employee, authorized access granted."));
    }
}
