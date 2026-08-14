package com.kary.moviebooking.service.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kary.moviebooking.exception.BadRequestException;
import com.kary.moviebooking.service.Interface.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder:cinebook/posters}")
    private String folder;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    @Override
    public String storeFile(MultipartFile file, String subFolder) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("No file was uploaded");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Only JPG, PNG and WEBP images are allowed");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BadRequestException("File size must be under 5MB");
        }

        try {
            // unique public_id so same-name uploads don't overwrite each other
            String publicId = folder + "/" + subFolder + "/" + UUID.randomUUID();

            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id",      publicId,
                            "overwrite",      true,
                            "resource_type",  "image",
                            // auto-optimize: convert to webp, compress, fit within 800x1200
                            "transformation", "f_auto,q_auto,c_limit,w_800,h_1200"
                    )
            );

            // secure_url = https Cloudinary CDN URL
            String url = (String) result.get("secure_url");
            log.info("Uploaded poster to Cloudinary: {}", url);
            return url;

        } catch (IOException e) {
            log.error("Cloudinary upload failed: {}", e.getMessage());
            throw new BadRequestException("Failed to upload image. Please try again.");
        }
    }
}
