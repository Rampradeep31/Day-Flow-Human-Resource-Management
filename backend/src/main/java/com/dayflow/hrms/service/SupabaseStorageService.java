package com.dayflow.hrms.service;

/**
 * Service interface for Supabase Storage operations.
 */
public interface SupabaseStorageService {

    /**
     * Uploads binary content to Supabase Storage.
     *
     * @param storagePath Relative path inside the bucket
     * @param data        Binary content
     * @param contentType MIME type of the file
     * @return storagePath on success
     */
    String uploadFile(String storagePath, byte[] data, String contentType);

    /**
     * Deletes a file object from Supabase Storage.
     *
     * @param storagePath Relative path inside the bucket
     */
    void deleteFile(String storagePath);

    /**
     * Generates a short-lived signed URL for private file access.
     *
     * @param storagePath      Relative path inside bucket
     * @param expiresInSeconds Duration of validity in seconds
     * @return Full signed URL string
     */
    String generateSignedUrl(String storagePath, int expiresInSeconds);
}
