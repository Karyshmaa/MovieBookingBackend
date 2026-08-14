package com.kary.moviebooking.service.Interface;

import com.kary.moviebooking.dto.SeatLayoutRequestDTO;
import com.kary.moviebooking.dto.SeatResponseDTO;

import java.util.List;

public interface SeatService {
    List<SeatResponseDTO> createSeatLayout(Long screenId, SeatLayoutRequestDTO request);

    List<SeatResponseDTO> getSeatsByScreenId(Long screenId);
}