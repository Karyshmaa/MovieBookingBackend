package com.kary.moviebooking.service.Interface;

import com.kary.moviebooking.dto.ScreenRequestDTO;
import com.kary.moviebooking.dto.ScreenResponseDTO;

import java.util.List;

public interface ScreenService {
    ScreenResponseDTO createScreen(ScreenRequestDTO request);
    ScreenResponseDTO getScreenById(Long id);
    List<ScreenResponseDTO> getAllScreens();
    List<ScreenResponseDTO> getScreensByTheaterId(Long theaterId);
    void deleteScreen(Long id);
}
