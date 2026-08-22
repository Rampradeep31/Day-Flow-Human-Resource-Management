package com.dayflow.hrms.controller;

import com.dayflow.hrms.dto.DocumentResponse;
import com.dayflow.hrms.dto.PageResponse;
import com.dayflow.hrms.entity.DocumentType;
import com.dayflow.hrms.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * REST Controller for Employee Document Management and Supabase Storage integration.
 */
@RestController
@RequestMapping("/api/v1/documents")
@Tag(name = "Documents", description = "Employee document upload, management, and secure retrieval endpoints")
@SecurityRequirement(name = "bearerAuth")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'ADMIN')")
    @Operation(summary = "Upload employee document", description = "Uploads a file to Supabase Storage and records document metadata in PostgreSQL")
    @ApiResponse(responseCode = "201", description = "Document uploaded successfully")
    @ApiResponse(responseCode = "400", description = "Invalid file or unsupported format")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @Parameter(description = "Document file binary (PDF, PNG, JPEG, DOC, DOCX, max 10MB)", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Type/Category of document (RESUME, IDENTITY, CONTRACT, CERTIFICATE, OTHER)", required = true)
            @RequestParam("documentType") DocumentType documentType) {

        DocumentResponse response = documentService.uploadDocument(file, documentType);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'ADMIN')")
    @Operation(summary = "Get own documents", description = "Retrieves paginated document list of the authenticated employee")
    @ApiResponse(responseCode = "200", description = "Documents retrieved")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<PageResponse<DocumentResponse>> getMyDocuments(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(documentService.getMyDocuments(pageable));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'ADMIN')")
    @Operation(summary = "Get employee documents", description = "Retrieves documents for a specific employee. Non-HR/Admin users can only view their own documents.")
    @ApiResponse(responseCode = "200", description = "Employee documents retrieved")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - Cannot view another employee's documents")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    public ResponseEntity<PageResponse<DocumentResponse>> getEmployeeDocuments(
            @PathVariable UUID employeeId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(documentService.getEmployeeDocuments(employeeId, pageable));
    }

    @GetMapping("/{documentId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'ADMIN')")
    @Operation(summary = "Get document by ID", description = "Retrieves single document metadata and signed download URL")
    @ApiResponse(responseCode = "200", description = "Document retrieved")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - Cannot view another employee's document")
    @ApiResponse(responseCode = "404", description = "Document not found")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable UUID documentId) {
        return ResponseEntity.ok(documentService.getDocumentById(documentId));
    }

    @DeleteMapping("/{documentId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'ADMIN')")
    @Operation(summary = "Delete document", description = "Deletes document metadata from database and file from Supabase Storage")
    @ApiResponse(responseCode = "204", description = "Document deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - Cannot delete another employee's document")
    @ApiResponse(responseCode = "404", description = "Document not found")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID documentId) {
        documentService.deleteDocument(documentId);
        return ResponseEntity.noContent().build();
    }
}
