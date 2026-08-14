package com.kary.moviebooking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScreenResponseDTO {
    private Long id;
    private String name;
    private Integer totalSeats;
    private Long theaterId;
    private String theaterName;
}
