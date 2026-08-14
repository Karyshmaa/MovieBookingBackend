package com.kary.moviebooking.controller.admin;

import com.kary.moviebooking.dto.TheaterRequestDTO;
import com.kary.moviebooking.dto.TheaterResponseDTO;
import com.kary.moviebooking.service.Interface.TheaterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/theaters")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminTheaterController {

    private final TheaterService theaterService;

    @PostMapping
    public ResponseEntity<TheaterResponseDTO> createTheater(
            @RequestBody @Valid TheaterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(theaterService.createTheater(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheater(@PathVariable Long id) {
        theaterService.deleteTheater(id);
        return ResponseEntity.noContent().build();
    }
}
