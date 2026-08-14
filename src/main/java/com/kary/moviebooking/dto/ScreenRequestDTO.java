package com.kary.moviebooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScreenRequestDTO {

    @NotBlank(message = "Screen name is required")
    private String name;

    @NotNull(message = "Theater ID is required")
    private Long theaterId;

    private Integer totalSeats;  // ✅ added
}