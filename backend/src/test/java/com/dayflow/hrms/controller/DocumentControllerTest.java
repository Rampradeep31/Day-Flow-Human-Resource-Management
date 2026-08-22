package com.dayflow.hrms.controller;

import com.dayflow.hrms.entity.*;
import com.dayflow.hrms.repository.*;
import com.dayflow.hrms.security.JwtService;
import com.dayflow.hrms.service.SupabaseStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupabaseStorageService storageService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DocumentRepository documentRepository;

    private User hrUser;
    private User employeeUser1;
    private User employeeUser2;

    private Employee employee1;
    private Employee employee2;

    private String hrToken;
    private String employeeToken1;
    private String employeeToken2;

    @BeforeEach
    void setUp() {
        documentRepository.deleteAll();
        employeeRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();

        Mockito.when(storageService.uploadFile(anyString(), any(byte[].class), anyString()))
                .thenAnswer(inv -> inv.getArgument(0));
        Mockito.when(storageService.generateSignedUrl(anyString(), anyInt()))
                .thenReturn("https://test.supabase.co/storage/v1/object/sign/sample.pdf");
        Mockito.doNothing().when(storageService).deleteFile(anyString());

        Role hrRole = roleRepository.findByName("HR").orElseThrow();
        Role employeeRole = roleRepository.findByName("EMPLOYEE").orElseThrow();

        // 1. HR User
        hrUser = userRepository.save(User.builder().email("hr.manager@dayflow.com").status(UserStatus.ACTIVE).build());
        userRoleRepository.save(new UserRole(hrUser, hrRole));
        hrToken = jwtService.generateToken(hrUser.getId(), hrUser.getEmail(), 3600000, Map.of("role", "authenticated"));

        // 2. Employee 1
        employeeUser1 = userRepository.save(User.builder().email("emp1@dayflow.com").status(UserStatus.ACTIVE).build());
        userRoleRepository.save(new UserRole(employeeUser1, employeeRole));
        employeeToken1 = jwtService.generateToken(employeeUser1.getId(), employeeUser1.getEmail(), 3600000, Map.of("role", "authenticated"));

        employee1 = employeeRepository.save(Employee.builder()
                .user(employeeUser1)
                .employeeCode("EMP101")
                .firstName("Dwight")
                .lastName("Schrute")
                .department("Sales")
                .designation("Assistant Regional Manager")
                .joiningDate(LocalDate.of(2021, 3, 10))
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build());

        // 3. Employee 2
        employeeUser2 = userRepository.save(User.builder().email("emp2@dayflow.com").status(UserStatus.ACTIVE).build());
        userRoleRepository.save(new UserRole(employeeUser2, employeeRole));
        employeeToken2 = jwtService.generateToken(employeeUser2.getId(), employeeUser2.getEmail(), 3600000, Map.of("role", "authenticated"));

        employee2 = employeeRepository.save(Employee.builder()
                .user(employeeUser2)
                .employeeCode("EMP102")
                .firstName("Jim")
                .lastName("Halpert")
                .department("Sales")
                .designation("Sales Representative")
                .joiningDate(LocalDate.of(2021, 4, 15))
                .employmentStatus(EmploymentStatus.ACTIVE)
                .build());
    }

    @Test
    @DisplayName("POST /api/v1/documents should upload valid PDF and return 201 Created")
    void shouldUploadValidDocument() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "Dummy PDF content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/documents")
                        .file(file)
                        .param("documentType", "RESUME")
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName", is("resume.pdf")))
                .andExpect(jsonPath("$.documentType", is("RESUME")))
                .andExpect(jsonPath("$.employeeCode", is("EMP101")))
                .andExpect(jsonPath("$.downloadUrl", is("https://test.supabase.co/storage/v1/object/sign/sample.pdf")));
    }

    @Test
    @DisplayName("POST /api/v1/documents with empty file should return 400 Bad Request")
    void shouldRejectEmptyFileUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.pdf",
                "application/pdf",
                new byte[0]
        );

        mockMvc.perform(multipart("/api/v1/documents")
                        .file(file)
                        .param("documentType", "RESUME")
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("BAD_REQUEST")));
    }

    @Test
    @DisplayName("POST /api/v1/documents with disallowed file extension should return 400 Bad Request")
    void shouldRejectDisallowedFileExtension() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "malicious.exe",
                "application/octet-stream",
                "Executable content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/documents")
                        .file(file)
                        .param("documentType", "OTHER")
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("BAD_REQUEST")));
    }

    @Test
    @DisplayName("GET /api/v1/documents/me should return documents of authenticated employee")
    void shouldGetMyDocuments() throws Exception {
        documentRepository.save(Document.builder()
                .employee(employee1)
                .fileName("resume.pdf")
                .storagePath("documents/EMP101/res.pdf")
                .contentType("application/pdf")
                .fileSize(1024L)
                .documentType(DocumentType.RESUME)
                .build());

        mockMvc.perform(get("/api/v1/documents/me")
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].fileName", is("resume.pdf")));
    }

    @Test
    @DisplayName("GET /api/v1/documents/employee/{id} should allow HR to view employee documents")
    void shouldAllowHrToViewEmployeeDocuments() throws Exception {
        documentRepository.save(Document.builder()
                .employee(employee1)
                .fileName("contract.pdf")
                .storagePath("documents/EMP101/contract.pdf")
                .contentType("application/pdf")
                .fileSize(2048L)
                .documentType(DocumentType.CONTRACT)
                .build());

        mockMvc.perform(get("/api/v1/documents/employee/" + employee1.getId())
                        .header("Authorization", "Bearer " + hrToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].fileName", is("contract.pdf")));
    }

    @Test
    @DisplayName("GET /api/v1/documents/employee/{id} should forbid employee from viewing another employee's documents (IDOR test)")
    void shouldForbidEmployeeFromViewingOtherEmployeeDocuments() throws Exception {
        mockMvc.perform(get("/api/v1/documents/employee/" + employee2.getId())
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode", is("FORBIDDEN")));
    }

    @Test
    @DisplayName("DELETE /api/v1/documents/{id} should allow employee to delete own document")
    void shouldAllowEmployeeToDeleteOwnDocument() throws Exception {
        Document doc = documentRepository.save(Document.builder()
                .employee(employee1)
                .fileName("temp.pdf")
                .storagePath("documents/EMP101/temp.pdf")
                .contentType("application/pdf")
                .fileSize(512L)
                .documentType(DocumentType.OTHER)
                .build());

        mockMvc.perform(delete("/api/v1/documents/" + doc.getId())
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isNoContent());

        Mockito.verify(storageService).deleteFile("documents/EMP101/temp.pdf");
    }

    @Test
    @DisplayName("DELETE /api/v1/documents/{id} should forbid employee from deleting another employee's document (IDOR test)")
    void shouldForbidEmployeeFromDeletingOtherEmployeeDocument() throws Exception {
        Document doc = documentRepository.save(Document.builder()
                .employee(employee2)
                .fileName("jim_doc.pdf")
                .storagePath("documents/EMP102/jim_doc.pdf")
                .contentType("application/pdf")
                .fileSize(512L)
                .documentType(DocumentType.OTHER)
                .build());

        mockMvc.perform(delete("/api/v1/documents/" + doc.getId())
                        .header("Authorization", "Bearer " + employeeToken1))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode", is("FORBIDDEN")));
    }

    @Test
    @DisplayName("GET /api/v1/documents/me without token should return 401 Unauthorized")
    void shouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/documents/me"))
                .andExpect(status().isUnauthorized());
    }
}
