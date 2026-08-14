package com.kary.moviebooking.service.Interface;

import com.kary.moviebooking.dto.ShowRequestDTO;
import com.kary.moviebooking.dto.ShowResponseDTO;

import java.util.List;

public interface ShowService {
    ShowResponseDTO createShow(ShowRequestDTO request);
    ShowResponseDTO getShowById(Long id);
    List<ShowResponseDTO> getAllShows();
    List<ShowResponseDTO> getShowsByMovieId(Long movieId);
    List<ShowResponseDTO> getShowsByTheaterId(Long theaterId);
    ShowResponseDTO updateShow(Long id, ShowRequestDTO request);
    void deleteShowById(Long id);
}
