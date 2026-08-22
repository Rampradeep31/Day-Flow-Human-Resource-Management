package com.dayflow.hrms.repository;

import com.dayflow.hrms.entity.LeaveRequest;
import com.dayflow.hrms.entity.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for LeaveRequest entity.
 */
@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, UUID>, JpaSpecificationExecutor<LeaveRequest> {

    @Override
    @EntityGraph(attributePaths = {"employee"})
    Page<LeaveRequest> findAll(org.springframework.data.jpa.domain.Specification<LeaveRequest> specification, Pageable pageable);

    @Query("SELECT lr FROM LeaveRequest lr JOIN FETCH lr.employee e JOIN FETCH e.user u LEFT JOIN FETCH lr.reviewedBy rb WHERE lr.id = :id")
    Optional<LeaveRequest> findByIdWithDetails(@Param("id") UUID id);

    @Query(value = "SELECT lr FROM LeaveRequest lr JOIN FETCH lr.employee e JOIN FETCH e.user u LEFT JOIN FETCH lr.reviewedBy rb WHERE e.id = :employeeId",
            countQuery = "SELECT count(lr) FROM LeaveRequest lr WHERE lr.employee.id = :employeeId")
    Page<LeaveRequest> findByEmployeeIdWithDetails(@Param("employeeId") UUID employeeId, Pageable pageable);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.id = :employeeId " +
            "AND lr.status IN (:statuses) " +
            "AND lr.startDate <= :endDate " +
            "AND lr.endDate >= :startDate")
    List<LeaveRequest> findOverlappingRequests(
            @Param("employeeId") UUID employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuses") Collection<LeaveStatus> statuses);

    default List<LeaveRequest> findActiveOverlappingRequests(UUID employeeId, LocalDate startDate, LocalDate endDate) {
        return findOverlappingRequests(employeeId, startDate, endDate, List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED));
    }

    @Query(value = "SELECT lr FROM LeaveRequest lr JOIN FETCH lr.employee e JOIN FETCH e.user u LEFT JOIN FETCH lr.reviewedBy rb " +
            "WHERE (:employeeId IS NULL OR e.id = :employeeId) " +
            "AND (:status IS NULL OR lr.status = :status) " +
            "AND (:startDate IS NULL OR lr.endDate >= :startDate) " +
            "AND (:endDate IS NULL OR lr.startDate <= :endDate)",
            countQuery = "SELECT count(lr) FROM LeaveRequest lr " +
            "WHERE (:employeeId IS NULL OR lr.employee.id = :employeeId) " +
            "AND (:status IS NULL OR lr.status = :status) " +
            "AND (:startDate IS NULL OR lr.endDate >= :startDate) " +
            "AND (:endDate IS NULL OR lr.startDate <= :endDate)")
    Page<LeaveRequest> findWithFilters(
            @Param("employeeId") UUID employeeId,
            @Param("status") LeaveStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    List<LeaveRequest> findByEmployeeIdAndStatus(UUID employeeId, LeaveStatus status);

    // ── Dashboard aggregate queries ──

    long countByEmployeeIdAndStatus(UUID employeeId, LeaveStatus status);

    long countByStatus(LeaveStatus status);

    @Query("SELECT COUNT(DISTINCT lr.employee.id) FROM LeaveRequest lr " +
            "WHERE lr.status = com.dayflow.hrms.entity.LeaveStatus.APPROVED " +
            "AND lr.startDate <= :today AND lr.endDate >= :today")
    long countEmployeesOnLeaveToday(@Param("today") LocalDate today);

    @Query(value = "SELECT lr FROM LeaveRequest lr JOIN FETCH lr.employee e JOIN FETCH e.user u LEFT JOIN FETCH lr.reviewedBy rb ORDER BY lr.updatedAt DESC",
            countQuery = "SELECT count(lr) FROM LeaveRequest lr")
    Page<LeaveRequest> findRecentWithDetails(Pageable pageable);

    @Query("SELECT COALESCE(lr.employee.department, 'Unassigned'), COUNT(lr), " +
           "SUM(CASE WHEN lr.status = com.dayflow.hrms.entity.LeaveStatus.APPROVED THEN 1 ELSE 0 END) " +
           "FROM LeaveRequest lr GROUP BY lr.employee.department")
    List<Object[]> getDepartmentLeaveReport();
}
