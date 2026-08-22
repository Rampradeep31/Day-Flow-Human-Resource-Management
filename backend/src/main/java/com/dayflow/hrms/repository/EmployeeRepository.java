package com.dayflow.hrms.repository;

import com.dayflow.hrms.entity.Employee;
import com.dayflow.hrms.entity.EmploymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for Employee entity.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID>, JpaSpecificationExecutor<Employee> {

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.user u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role WHERE e.id = :id")
    Optional<Employee> findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.user u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role WHERE e.user.id = :userId")
    Optional<Employee> findByUserIdWithDetails(@Param("userId") UUID userId);

    Optional<Employee> findByUserId(UUID userId);

    Optional<Employee> findByEmployeeCode(String employeeCode);

    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByUserId(UUID userId);

    @Query("SELECT e FROM Employee e " +
           "WHERE (cast(:search as string) IS NULL OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', cast(:search as string), '%')) " +
           "      OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', cast(:search as string), '%')) " +
           "      OR LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', cast(:search as string), '%')) " +
           "      OR LOWER(e.user.email) LIKE LOWER(CONCAT('%', cast(:search as string), '%'))) " +
           "  AND (:department IS NULL OR e.department = :department) " +
           "  AND (:status IS NULL OR e.employmentStatus = :status)")
    Page<Employee> findWithFilters(
            @Param("search") String search,
            @Param("department") String department,
            @Param("status") EmploymentStatus status,
            Pageable pageable
    );

    // ── Dashboard aggregate queries ──

    long countByEmploymentStatus(EmploymentStatus status);

    @Query("SELECT COALESCE(e.department, 'Unassigned'), COUNT(e) FROM Employee e GROUP BY e.department ORDER BY COUNT(e) DESC")
    List<Object[]> countByDepartment();

    @Query("SELECT COALESCE(e.department, 'Unassigned'), COUNT(e), " +
           "SUM(CASE WHEN e.employmentStatus = com.dayflow.hrms.entity.EmploymentStatus.ACTIVE THEN 1 ELSE 0 END) " +
           "FROM Employee e GROUP BY e.department ORDER BY COALESCE(e.department, 'Unassigned')")
    List<Object[]> getDepartmentEmployeeReport();
}
