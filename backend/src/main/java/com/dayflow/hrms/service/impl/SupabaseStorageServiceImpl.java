package com.dayflow.hrms.service.impl;

import com.dayflow.hrms.service.SupabaseStorageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Implementation of SupabaseStorageService managing direct REST API calls
 * to Supabase Storage.
 */
@Service
public class SupabaseStorageServiceImpl implements SupabaseStorageService {

    private static final Logger log = LoggerFactory.getLogger(SupabaseStorageServiceImpl.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String supabaseUrl;
    private final String serviceRoleKey;
    private final String bucketName;

    public SupabaseStorageServiceImpl(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.service-role-key}") String serviceRoleKey,
            @Value("${supabase.storage.bucket:dayflow-documents}") String bucketName,
            ObjectMapper objectMapper) {
        this.supabaseUrl = supabaseUrl != null ? supabaseUrl.replaceAll("/+$", "") : "";
        this.serviceRoleKey = serviceRoleKey;
        this.bucketName = bucketName;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public String uploadFile(String storagePath, byte[] data, String contentType) {
        String cleanPath = storagePath.startsWith("/") ? storagePath.substring(1) : storagePath;
        String url = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + cleanPath;

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", serviceRoleKey);
        headers.set("Authorization", "Bearer " + serviceRoleKey);
        headers.set("x-upsert", "true");
        if (contentType != null && !contentType.isBlank()) {
            headers.setContentType(MediaType.parseMediaType(contentType));
        } else {
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }

        HttpEntity<byte[]> entity = new HttpEntity<>(data, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully uploaded file to Supabase Storage: {}/{}", bucketName, cleanPath);
                return cleanPath;
            } else {
                log.error("Failed to upload file to Supabase Storage. Status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to upload file to Supabase Storage: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error communicating with Supabase Storage upload API: {}", e.getMessage());
            throw new RuntimeException("Storage upload error: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String storagePath) {
        String cleanPath = storagePath.startsWith("/") ? storagePath.substring(1) : storagePath;
        String url = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + cleanPath;

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", serviceRoleKey);
        headers.set("Authorization", "Bearer " + serviceRoleKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            log.info("Successfully deleted file from Supabase Storage: {}/{}", bucketName, cleanPath);
        } catch (Exception e) {
            log.warn("Warning deleting file from Supabase Storage ({}): {}", cleanPath, e.getMessage());
        }
    }

    @Override
    public String generateSignedUrl(String storagePath, int expiresInSeconds) {
        String cleanPath = storagePath.startsWith("/") ? storagePath.substring(1) : storagePath;
        String url = supabaseUrl + "/storage/v1/object/sign/" + bucketName + "/" + cleanPath;

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", serviceRoleKey);
        headers.set("Authorization", "Bearer " + serviceRoleKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of("expiresIn", expiresInSeconds > 0 ? expiresInSeconds : 3600);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode json = objectMapper.readTree(response.getBody());
                String signedUrlPath = json.path("signedURL").asText();
                if (signedUrlPath != null && !signedUrlPath.isBlank()) {
                    return supabaseUrl + "/storage/v1" + signedUrlPath;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to generate signed URL for path {}: {}", cleanPath, e.getMessage());
        }
        return null;
    }
}
