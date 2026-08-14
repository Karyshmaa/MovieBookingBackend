package com.kary.moviebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatResponseDTO {
    private Long id;
    private String rowNumber;
    private int seatNumber;
    private String seatLabel;
    private String seatType;
    private Long screenId;
}
