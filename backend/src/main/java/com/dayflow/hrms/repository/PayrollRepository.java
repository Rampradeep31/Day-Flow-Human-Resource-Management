package com.dayflow.hrms.repository;

import com.dayflow.hrms.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for Payroll entity.
 */
@Repository
public interface PayrollRepository extends JpaRepository<Payroll, UUID>, JpaSpecificationExecutor<Payroll> {

    @Override
    @EntityGraph(attributePaths = {"employee"})
    Page<Payroll> findAll(org.springframework.data.jpa.domain.Specification<Payroll> specification, Pageable pageable);

    Optional<Payroll> findByEmployeeId(UUID employeeId);

    @Query("SELECT p FROM Payroll p JOIN FETCH p.employee e JOIN FETCH e.user u WHERE e.id = :employeeId")
    Optional<Payroll> findByEmployeeIdWithDetails(@Param("employeeId") UUID employeeId);

    boolean existsByEmployeeId(UUID employeeId);

    // ── Dashboard aggregate queries ──

    @Query("SELECT COALESCE(SUM(p.baseSalary), 0), COALESCE(SUM(p.allowances), 0), " +
            "COALESCE(SUM(p.deductions), 0), COALESCE(SUM(p.netSalary), 0) FROM Payroll p")
    List<Object[]> getAggregatePayrollSummary();

    @Query("SELECT COALESCE(p.employee.department, 'Unassigned'), " +
           "COALESCE(SUM(p.baseSalary), 0), COALESCE(SUM(p.allowances), 0), " +
           "COALESCE(SUM(p.deductions), 0), COALESCE(SUM(p.netSalary), 0) " +
           "FROM Payroll p GROUP BY p.employee.department")
    List<Object[]> getDepartmentPayrollReport();
}
