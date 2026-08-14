package com.kary.moviebooking.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MovieRequestDTO {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Language is required")
    private String language;

    private String director;

    @Min(value = 1900, message = "Invalid release year")
    private int releaseYear;

    private String genre;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private int duration;

    @Min(value = 0) @Max(value = 10)
    private BigDecimal rating;

    private String about;

    private String posterUrl;
}
