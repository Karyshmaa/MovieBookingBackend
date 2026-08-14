package com.kary.moviebooking.controller.admin;

import com.kary.moviebooking.dto.MovieRequestDTO;
import com.kary.moviebooking.dto.MovieResponseDTO;
import com.kary.moviebooking.exception.BadRequestException;
import com.kary.moviebooking.service.Interface.FileStorageService;
import com.kary.moviebooking.service.Interface.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/movies")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminMovieController {

    private final MovieService movieService;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB in bytes
    private final FileStorageService fileStorageService;

    @PostMapping
    public ResponseEntity<MovieResponseDTO> createMovie(
            @RequestBody @Valid MovieRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movieService.createMovie(request));
    }

    @PostMapping("/upload-poster")
    public ResponseEntity<Map<String, String>> uploadPoster(
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Only JPEG, PNG, or WEBP images are allowed");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size must not exceed 5MB");
        }

        String url = fileStorageService.storeFile(file, "movies");
        return ResponseEntity.ok(Map.of("posterUrl", url));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> updateMovie(
            @PathVariable Long id,
            @RequestBody @Valid MovieRequestDTO request) {
        return ResponseEntity.ok(movieService.updateMovie(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}
