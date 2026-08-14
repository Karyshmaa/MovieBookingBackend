package com.kary.moviebooking.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MovieResponseDTO {
    private Long id;
    private String title;
    private String language;
    private String director;
    private int releaseYear;
    private String genre;
    private int duration;
    private BigDecimal rating;
    private String about;
    private String posterUrl;
    private LocalDateTime createdAt;
}
