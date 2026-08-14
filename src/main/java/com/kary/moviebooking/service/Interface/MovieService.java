package com.kary.moviebooking.service.Interface;

import com.kary.moviebooking.dto.MovieRequestDTO;
import com.kary.moviebooking.dto.MovieResponseDTO;

import java.util.List;

public interface MovieService {
    MovieResponseDTO createMovie(MovieRequestDTO request);
    MovieResponseDTO getMovieById(Long id);
    List<MovieResponseDTO> getAllMovies();
    MovieResponseDTO updateMovie(Long id, MovieRequestDTO request);
    void deleteMovie(Long id);
}
