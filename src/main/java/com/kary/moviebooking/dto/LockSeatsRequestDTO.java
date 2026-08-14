package com.kary.moviebooking.dto;

import lombok.Data;
import java.util.List;

@Data
public class LockSeatsRequestDTO {

    private Long showId;
    private List<Long> seatIds;
    private Long userId;

}
