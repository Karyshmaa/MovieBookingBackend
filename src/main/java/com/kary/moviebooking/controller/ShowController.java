package com.kary.moviebooking.controller;

import com.kary.moviebooking.dto.ShowResponseDTO;
import com.kary.moviebooking.service.Interface.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    @GetMapping("/{id}")
    public ResponseEntity<ShowResponseDTO> getShowById(@PathVariable Long id) {
        return ResponseEntity.ok(showService.getShowById(id));
    }

    @GetMapping
    public ResponseEntity<List<ShowResponseDTO>> getAllShows() {
        return ResponseEntity.ok(showService.getAllShows());
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ShowResponseDTO>> getShowsByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(showService.getShowsByMovieId(movieId));
    }

    @GetMapping("/theater/{theaterId}")
    public ResponseEntity<List<ShowResponseDTO>> getShowsByTheater(@PathVariable Long theaterId) {
        return ResponseEntity.ok(showService.getShowsByTheaterId(theaterId));
    }
}
