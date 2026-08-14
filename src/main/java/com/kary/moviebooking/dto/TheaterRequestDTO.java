package com.kary.moviebooking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TheaterRequestDTO {
    @NotBlank(message = "Theater name is required")
    private String name;

    @NotBlank(message = "Location is required")
    private String location;
}
