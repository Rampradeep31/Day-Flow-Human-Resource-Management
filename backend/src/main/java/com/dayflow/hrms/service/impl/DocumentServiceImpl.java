package com.dayflow.hrms.service.impl;

import com.dayflow.hrms.dto.DocumentResponse;
import com.dayflow.hrms.dto.PageResponse;
import com.dayflow.hrms.entity.Document;
import com.dayflow.hrms.entity.DocumentType;
import com.dayflow.hrms.entity.Employee;
import com.dayflow.hrms.exception.BadRequestException;
import com.dayflow.hrms.exception.ResourceNotFoundException;
import com.dayflow.hrms.repository.DocumentRepository;
import com.dayflow.hrms.repository.EmployeeRepository;
import com.dayflow.hrms.security.SecurityUtils;
import com.dayflow.hrms.security.UserPrincipal;
import com.dayflow.hrms.service.DocumentService;
import com.dayflow.hrms.service.SupabaseStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of DocumentService providing file validation,
 * Supabase Storage integration, and ownership protection.
 */
@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "application/pdf",
            "image/png",
            "image/jpeg",
            "image/jpg",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".pdf",
            ".png",
            ".jpg",
            ".jpeg",
            ".doc",
            ".docx"
    );

    private final DocumentRepository documentRepository;
    private final EmployeeRepository employeeRepository;
    private final SupabaseStorageService storageService;
    private final long maxFileSize;

    public DocumentServiceImpl(
            DocumentRepository documentRepository,
            EmployeeRepository employeeRepository,
            SupabaseStorageService storageService,
            @Value("${dayflow.documents.max-file-size:10485760}") long maxFileSize) {
        this.documentRepository = documentRepository;
        this.employeeRepository = employeeRepository;
        this.storageService = storageService;
        this.maxFileSize = maxFileSize;
    }

    @Override
    public DocumentResponse uploadDocument(MultipartFile file, DocumentType documentType) {
        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));

        Employee employee = employeeRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found for user: " + currentUserId));

        if (documentType == null) {
            throw new BadRequestException("Document type is required (RESUME, IDENTITY, CONTRACT, CERTIFICATE, OTHER)");
        }

        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String safeFileName = sanitizeFileName(originalFilename);
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        // Generate server-controlled unique storage path
        String storagePath = "documents/" + employee.getId() + "/" + UUID.randomUUID() + "_" + safeFileName;

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            log.error("Failed to read bytes from uploaded file: {}", e.getMessage());
            throw new BadRequestException("Could not read uploaded file content");
        }

        // 1. Upload to Supabase Storage
        String uploadedPath = storageService.uploadFile(storagePath, bytes, contentType);

        // 2. Persist metadata in PostgreSQL with cleanup rollback on failure
        Document document = Document.builder()
                .employee(employee)
                .fileName(safeFileName)
                .storagePath(uploadedPath)
                .contentType(contentType)
                .fileSize(file.getSize())
                .documentType(documentType)
                .build();

        Document saved;
        try {
            saved = documentRepository.save(document);
            log.info("Document saved successfully: ID={}, Employee={}, File={}",
                    saved.getId(), employee.getEmployeeCode(), saved.getFileName());
        } catch (Exception ex) {
            log.error("Failed to persist document metadata in database. Rolling back storage file: {}", uploadedPath, ex);
            storageService.deleteFile(uploadedPath);
            throw ex;
        }

        String signedUrl = storageService.generateSignedUrl(saved.getStoragePath(), 3600);
        return DocumentResponse.fromEntity(saved, signedUrl);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DocumentResponse> getMyDocuments(Pageable pageable) {
        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));

        Employee employee = employeeRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found for user: " + currentUserId));

        Page<Document> page = documentRepository.findByEmployeeIdWithDetails(employee.getId(), pageable);
        return PageResponse.of(page.map(doc -> {
            String signedUrl = storageService.generateSignedUrl(doc.getStoragePath(), 3600);
            return DocumentResponse.fromEntity(doc, signedUrl);
        }));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DocumentResponse> getEmployeeDocuments(UUID employeeId, Pageable pageable) {
        Employee employee = employeeRepository.findByIdWithDetails(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        UserPrincipal principal = SecurityUtils.getCurrentUserPrincipal()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));

        boolean isHrOrAdmin = principal.getRoleNames().contains("HR") || principal.getRoleNames().contains("ADMIN");
        boolean isOwner = employee.getUser() != null && employee.getUser().getId().equals(principal.getId());

        if (!isHrOrAdmin && !isOwner) {
            log.warn("Access denied: User {} attempted to view documents for employee {}",
                    principal.getEmail(), employee.getEmployeeCode());
            throw new AccessDeniedException("You do not have permission to view other employee documents");
        }

        Page<Document> page = documentRepository.findByEmployeeIdWithDetails(employeeId, pageable);
        return PageResponse.of(page.map(doc -> {
            String signedUrl = storageService.generateSignedUrl(doc.getStoragePath(), 3600);
            return DocumentResponse.fromEntity(doc, signedUrl);
        }));
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getDocumentById(UUID documentId) {
        Document document = documentRepository.findByIdWithDetails(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + documentId));

        UserPrincipal principal = SecurityUtils.getCurrentUserPrincipal()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));

        boolean isHrOrAdmin = principal.getRoleNames().contains("HR") || principal.getRoleNames().contains("ADMIN");
        boolean isOwner = document.getEmployee().getUser() != null &&
                document.getEmployee().getUser().getId().equals(principal.getId());

        if (!isHrOrAdmin && !isOwner) {
            log.warn("Access denied: User {} attempted to view document {}", principal.getEmail(), documentId);
            throw new AccessDeniedException("You do not have permission to view this document");
        }

        String signedUrl = storageService.generateSignedUrl(document.getStoragePath(), 3600);
        return DocumentResponse.fromEntity(document, signedUrl);
    }

    @Override
    public void deleteDocument(UUID documentId) {
        Document document = documentRepository.findByIdWithDetails(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + documentId));

        UserPrincipal principal = SecurityUtils.getCurrentUserPrincipal()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated"));

        boolean isHrOrAdmin = principal.getRoleNames().contains("HR") || principal.getRoleNames().contains("ADMIN");
        boolean isOwner = document.getEmployee().getUser() != null &&
                document.getEmployee().getUser().getId().equals(principal.getId());

        if (!isHrOrAdmin && !isOwner) {
            log.warn("Access denied: User {} attempted to delete document {}", principal.getEmail(), documentId);
            throw new AccessDeniedException("You do not have permission to delete this document");
        }

        String storagePath = document.getStoragePath();
        documentRepository.delete(document);
        storageService.deleteFile(storagePath);
        log.info("Deleted document {} for employee {}", documentId, document.getEmployee().getEmployeeCode());
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file cannot be empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new BadRequestException("File size exceeds maximum allowed limit of " + (maxFileSize / (1024 * 1024)) + "MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BadRequestException("File name cannot be empty");
        }

        String lowerName = originalFilename.toLowerCase(Locale.ROOT);
        boolean hasAllowedExtension = ALLOWED_EXTENSIONS.stream().anyMatch(lowerName::endsWith);
        if (!hasAllowedExtension) {
            throw new BadRequestException("Unsupported file extension. Allowed extensions: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        String contentType = file.getContentType();
        if (contentType != null && !contentType.isBlank() && !contentType.equals("application/octet-stream")) {
            String lowerMime = contentType.toLowerCase(Locale.ROOT);
            if (!ALLOWED_MIME_TYPES.contains(lowerMime)) {
                throw new BadRequestException("Unsupported file content type: " + contentType + ". Allowed types: PDF, PNG, JPEG, DOC, DOCX");
            }
        }
    }

    private String sanitizeFileName(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "document";
        }
        // Remove directory paths and replace special characters
        String nameWithoutPath = originalFilename.replace('\\', '/');
        if (nameWithoutPath.contains("/")) {
            nameWithoutPath = nameWithoutPath.substring(nameWithoutPath.lastIndexOf('/') + 1);
        }
        String clean = nameWithoutPath.replaceAll("[^a-zA-Z0-9._-]", "_");
        return clean.length() > 200 ? clean.substring(0, 200) : clean;
    }
}
