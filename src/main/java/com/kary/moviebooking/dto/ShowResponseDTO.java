package com.kary.moviebooking.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ShowResponseDTO {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private String moviePosterUrl;
    private Long screenId;
    private String screenName;
    private Long theaterId;
    private String theaterName;
    private String location;
    private LocalDateTime showTime;
}
