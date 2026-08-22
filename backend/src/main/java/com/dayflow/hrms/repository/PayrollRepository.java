package com.dayflow.hrms.repository;

import com.dayflow.hrms.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for Payroll entity.
 */
@Repository
public interface PayrollRepository extends JpaRepository<Payroll, UUID> {

    Optional<Payroll> findByEmployeeId(UUID employeeId);

    @Query("SELECT p FROM Payroll p JOIN FETCH p.employee e JOIN FETCH e.user u WHERE e.id = :employeeId")
    Optional<Payroll> findByEmployeeIdWithDetails(@Param("employeeId") UUID employeeId);

    boolean existsByEmployeeId(UUID employeeId);
}
