package com.kary.moviebooking.controller.admin;

import com.kary.moviebooking.dto.ScreenRequestDTO;
import com.kary.moviebooking.dto.ScreenResponseDTO;
import com.kary.moviebooking.dto.SeatLayoutRequestDTO;
import com.kary.moviebooking.dto.SeatResponseDTO;
import com.kary.moviebooking.service.Interface.ScreenService;
import com.kary.moviebooking.service.Interface.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/screens")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminScreenController {

    private final ScreenService screenService;
    private final SeatService seatService;

    @PostMapping
    public ResponseEntity<ScreenResponseDTO> createScreen(
            @RequestBody @Valid ScreenRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(screenService.createScreen(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScreen(@PathVariable Long id) {
        screenService.deleteScreen(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/seats")
    public ResponseEntity<List<SeatResponseDTO>> createSeatLayout(
            @PathVariable Long id,
            @RequestBody @Valid SeatLayoutRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(seatService.createSeatLayout(id, request));
    }
}
