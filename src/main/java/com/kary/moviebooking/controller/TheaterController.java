package com.kary.moviebooking.controller;

import com.kary.moviebooking.dto.TheaterResponseDTO;
import com.kary.moviebooking.service.Interface.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theaters")
@RequiredArgsConstructor
public class TheaterController {

    private final TheaterService theaterService;

    @GetMapping("/{id}")
    public ResponseEntity<TheaterResponseDTO> getTheaterById(@PathVariable Long id) {
        return ResponseEntity.ok(theaterService.getTheaterById(id));
    }

    @GetMapping
    public ResponseEntity<List<TheaterResponseDTO>> getAllTheaters() {
        return ResponseEntity.ok(theaterService.getAllTheaters());
    }
}
