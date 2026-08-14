package com.kary.moviebooking.dto;

import lombok.Data;
//import org.antlr.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Data
public class BookingRequestDTO {

    @NotNull(message = "Show ID is required")
    private Long showId;

    @NotEmpty(message = "Select at least one seat")
    private List<Long> seatIds;
}

