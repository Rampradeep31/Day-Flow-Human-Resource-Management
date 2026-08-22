package com.dayflow.hrms.repository;

import com.dayflow.hrms.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DocumentRepositoryTest {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    private User testUser;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        documentRepository.deleteAll();
        employeeRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();

        Role employeeRole = roleRepository.findByName("EMPLOYEE").orElseThrow();

        testUser = userRepository.save(User.builder()
                .email("dwight.schrute@dayflow.com")
                .status(UserStatus.ACTIVE)
                .build());
        userRoleRepository.save(new UserRole(testUser, employeeRole));

        testEmployee = employeeRepository.save(Employee.builder()
                .user(testUser)
                .employeeCode("EMP101")
                .firstName("Dwight")
                .lastName("Schrute")
                .department("Sales")
                .designation("Assistant Regional Manager")
                .joiningDate(LocalDate.of(2021, 3, 10))
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build());
    }

    @Test
    @DisplayName("Should persist and retrieve Document entity with eager details")
    void shouldPersistAndRetrieveDocument() {
        Document doc = Document.builder()
                .employee(testEmployee)
                .fileName("resume.pdf")
                .storagePath("documents/EMP101/uuid_resume.pdf")
                .contentType("application/pdf")
                .fileSize(102400L)
                .documentType(DocumentType.RESUME)
                .build();

        Document saved = documentRepository.save(doc);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();

        Optional<Document> retrieved = documentRepository.findByIdWithDetails(saved.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getEmployee().getEmployeeCode()).isEqualTo("EMP101");
        assertThat(retrieved.get().getFileName()).isEqualTo("resume.pdf");
        assertThat(retrieved.get().getDocumentType()).isEqualTo(DocumentType.RESUME);
    }

    @Test
    @DisplayName("Should retrieve paginated documents for employee")
    void shouldFindDocumentsByEmployeeId() {
        documentRepository.save(Document.builder()
                .employee(testEmployee)
                .fileName("doc1.pdf")
                .storagePath("documents/path1.pdf")
                .contentType("application/pdf")
                .fileSize(1024L)
                .documentType(DocumentType.RESUME)
                .build());

        documentRepository.save(Document.builder()
                .employee(testEmployee)
                .fileName("doc2.png")
                .storagePath("documents/path2.png")
                .contentType("image/png")
                .fileSize(2048L)
                .documentType(DocumentType.IDENTITY)
                .build());

        Page<Document> page = documentRepository.findByEmployeeIdWithDetails(testEmployee.getId(), PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(2);
    }
}
