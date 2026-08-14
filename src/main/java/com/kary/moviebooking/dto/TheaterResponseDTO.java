package com.kary.moviebooking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TheaterResponseDTO {
    private Long id;
    private String name;
    private String location;
}
