package com.dayflow.hrms.controller;

import com.dayflow.hrms.dto.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health", description = "Application health check endpoints")
public class HealthController {

    @GetMapping
    @Operation(
        summary = "Check backend service health",
        description = "Returns current operational status of the Dayflow HRMS backend service"
    )
    @ApiResponse(responseCode = "200", description = "Service is up and operational")
    public ResponseEntity<HealthResponse> getHealth() {
        HealthResponse response = HealthResponse.builder()
                .status("UP")
                .service("dayflow-backend")
                .build();
        return ResponseEntity.ok(response);
    }
}
