package com.kary.moviebooking.service.Interface;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * Stores the given file on disk and returns a publicly accessible URL path
     * (e.g. "/uploads/posters/xyz.jpg") that the frontend can use directly in an <img> tag.
     */
    String storeFile(MultipartFile file, String subFolder);
}
