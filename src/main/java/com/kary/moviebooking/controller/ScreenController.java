package com.kary.moviebooking.controller;

import com.kary.moviebooking.dto.ScreenResponseDTO;
import com.kary.moviebooking.dto.SeatResponseDTO;
import com.kary.moviebooking.service.Interface.ScreenService;
import com.kary.moviebooking.service.Interface.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screens")
@RequiredArgsConstructor
public class ScreenController {

    private final ScreenService screenService;
    private final SeatService seatService;

    @GetMapping("/{id}")
    public ResponseEntity<ScreenResponseDTO> getScreenById(@PathVariable Long id) {
        return ResponseEntity.ok(screenService.getScreenById(id));
    }

    @GetMapping
    public ResponseEntity<List<ScreenResponseDTO>> getAllScreens() {
        return ResponseEntity.ok(screenService.getAllScreens());
    }

    @GetMapping("/theater/{theaterId}")
    public ResponseEntity<List<ScreenResponseDTO>> getScreensByTheaterId(
            @PathVariable Long theaterId) {
        return ResponseEntity.ok(screenService.getScreensByTheaterId(theaterId));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<SeatResponseDTO>> getSeatsForScreen(@PathVariable Long id) {
        return ResponseEntity.ok(seatService.getSeatsByScreenId(id));
    }
}
