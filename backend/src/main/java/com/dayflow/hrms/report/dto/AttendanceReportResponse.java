package com.dayflow.hrms.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Attendance report row. The current schema has no attendance records, so this report is empty.")
public record AttendanceReportResponse(
        UUID employeeId,
        String employeeCode,
        String employeeName,
        LocalDate date,
        Instant checkIn,
        Instant checkOut,
        Double workingHours,
        String attendanceStatus) {
}
