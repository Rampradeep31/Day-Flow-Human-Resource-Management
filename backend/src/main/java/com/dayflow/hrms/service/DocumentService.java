package com.dayflow.hrms.service;

import com.dayflow.hrms.dto.DocumentResponse;
import com.dayflow.hrms.dto.PageResponse;
import com.dayflow.hrms.entity.DocumentType;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Service interface for Employee Document Management operations.
 */
public interface DocumentService {

    DocumentResponse uploadDocument(MultipartFile file, DocumentType documentType);

    PageResponse<DocumentResponse> getMyDocuments(Pageable pageable);

    PageResponse<DocumentResponse> getEmployeeDocuments(UUID employeeId, Pageable pageable);

    DocumentResponse getDocumentById(UUID documentId);

    void deleteDocument(UUID documentId);
}
