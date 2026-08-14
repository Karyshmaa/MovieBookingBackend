package com.kary.moviebooking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SeatLayoutRequestDTO {

    @NotEmpty(message = "At least one row configuration is required")
    private List<RowConfig> rows;

    @Data
    public static class RowConfig {
        @NotNull(message = "Row label is required")
        private String rowNumber;     // e.g. "A"

        @Min(value = 1, message = "Seats per row must be at least 1")
        private int seatCount;        // e.g. 10 -> seats 1..10

        @NotNull(message = "Seat type is required")
        private String seatType;      // NORMAL, PREMIUM, RECLINER
    }
}
