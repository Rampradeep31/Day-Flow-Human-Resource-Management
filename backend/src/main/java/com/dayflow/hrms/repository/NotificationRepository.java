package com.dayflow.hrms.repository;

import com.dayflow.hrms.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for Notification entity.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @Query("SELECT n FROM Notification n JOIN FETCH n.employee e JOIN FETCH e.user u WHERE n.id = :id")
    Optional<Notification> findByIdWithDetails(@Param("id") UUID id);

    @Query(value = "SELECT n FROM Notification n JOIN FETCH n.employee e JOIN FETCH e.user u WHERE e.id = :employeeId",
            countQuery = "SELECT count(n) FROM Notification n WHERE n.employee.id = :employeeId")
    Page<Notification> findByEmployeeIdWithDetails(@Param("employeeId") UUID employeeId, Pageable pageable);

    long countByEmployeeIdAndIsReadFalse(UUID employeeId);

    // ── Dashboard aggregate queries ──

    @Query("SELECT n FROM Notification n WHERE n.employee.id = :employeeId ORDER BY n.createdAt DESC")
    List<Notification> findRecentByEmployeeId(@Param("employeeId") UUID employeeId, Pageable pageable);
}
