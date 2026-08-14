package com.kary.moviebooking.service.Interface;

import com.kary.moviebooking.dto.ShowSeatResponseDTO;
import com.kary.moviebooking.entity.Show;

import java.util.List;

public interface ShowSeatService {
    void createShowSeats(Show show);

    void deleteShowSeatsByShowId(Long showId);

    void lockSeatsForUser(List<Long> seatIds, Long showId, String username);

    List<ShowSeatResponseDTO> getSeatMapForShow(Long showId);
}
