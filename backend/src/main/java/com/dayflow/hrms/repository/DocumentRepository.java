package com.dayflow.hrms.repository;

import com.dayflow.hrms.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for Document entity.
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    @Query("SELECT d FROM Document d JOIN FETCH d.employee e JOIN FETCH e.user u WHERE d.id = :id")
    Optional<Document> findByIdWithDetails(@Param("id") UUID id);

    @Query(value = "SELECT d FROM Document d JOIN FETCH d.employee e JOIN FETCH e.user u WHERE e.id = :employeeId",
            countQuery = "SELECT count(d) FROM Document d WHERE d.employee.id = :employeeId")
    Page<Document> findByEmployeeIdWithDetails(@Param("employeeId") UUID employeeId, Pageable pageable);

    Page<Document> findByEmployeeId(UUID employeeId, Pageable pageable);
}
