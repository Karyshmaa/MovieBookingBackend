package com.kary.moviebooking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShowRequestDTO {

    @NotNull(message = "Movie ID is required")
    private Long movieId;

    @NotNull(message = "Theater ID is required")
    private Long theaterId;

    @NotNull(message = "Screen ID is required")
    private Long screenId;

    @NotNull(message = "Show time is required")
    private LocalDateTime showTime;
}
