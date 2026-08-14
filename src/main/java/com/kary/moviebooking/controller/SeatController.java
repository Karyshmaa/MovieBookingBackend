package com.kary.moviebooking.controller;

import com.kary.moviebooking.dto.ShowSeatResponseDTO;
import com.kary.moviebooking.service.Interface.ShowSeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final ShowSeatService showSeatService;

    // ✅ public — anyone browsing a show can see which seats are taken
    @GetMapping("/show/{showId}")
    public ResponseEntity<List<ShowSeatResponseDTO>> getSeatMapForShow(@PathVariable Long showId) {
        return ResponseEntity.ok(showSeatService.getSeatMapForShow(showId));
    }

    // ✅ requires login — temp-locks seats for 5 minutes while the user pays
    @PostMapping("/lock")
    public ResponseEntity<String> lockSeats(
            @RequestParam Long showId,
            @RequestParam List<Long> seatIds,
            @AuthenticationPrincipal UserDetails userDetails) {

        showSeatService.lockSeatsForUser(seatIds, showId, userDetails.getUsername());
        return ResponseEntity.ok("Seats locked successfully");
    }
}
