package com.kary.moviebooking.service.Interface;

import com.kary.moviebooking.dto.TheaterRequestDTO;
import com.kary.moviebooking.dto.TheaterResponseDTO;

import java.util.List;

public interface TheaterService {
    TheaterResponseDTO createTheater(TheaterRequestDTO request);
    TheaterResponseDTO getTheaterById(Long id);
    List<TheaterResponseDTO> getAllTheaters();
    void deleteTheater(Long id);
}
