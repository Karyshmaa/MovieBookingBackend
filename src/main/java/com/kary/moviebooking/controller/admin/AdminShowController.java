package com.kary.moviebooking.controller.admin;

import com.kary.moviebooking.dto.ShowRequestDTO;
import com.kary.moviebooking.dto.ShowResponseDTO;
import com.kary.moviebooking.service.Interface.ShowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/shows")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminShowController {

    private final ShowService showService;

    @PostMapping
    public ResponseEntity<ShowResponseDTO> createShow(
            @RequestBody @Valid ShowRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(showService.createShow(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShowResponseDTO> updateShow(
            @PathVariable Long id,
            @RequestBody @Valid ShowRequestDTO request) {
        return ResponseEntity.ok(showService.updateShow(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShow(@PathVariable Long id) {
        showService.deleteShowById(id);
        return ResponseEntity.noContent().build();
    }
}
