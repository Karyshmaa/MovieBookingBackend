package com.kary.moviebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShowSeatResponseDTO {
    private Long showSeatId;
    private String rowNumber;
    private int seatNumber;
    private String seatLabel;     // e.g. "A1"
    private String seatType;      // NORMAL, PREMIUM, RECLINER
    private String seatStatus;    // AVAILABLE, TEMP_LOCKED, BOOKED
    private Double price;
}
