package com.kary.moviebooking.service.Impl;

import com.kary.moviebooking.dto.MovieRequestDTO;
import com.kary.moviebooking.dto.MovieResponseDTO;
import com.kary.moviebooking.entity.Movie;
import com.kary.moviebooking.exception.ResourceNotFoundException;
import com.kary.moviebooking.repository.MovieRepository;
import com.kary.moviebooking.service.Interface.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public MovieResponseDTO createMovie(MovieRequestDTO request) {
        Movie movie = new Movie();
        movie.setTitle(request.getTitle());
        movie.setLanguage(request.getLanguage());
        movie.setDirector(request.getDirector());
        movie.setReleaseYear(request.getReleaseYear());
        movie.setGenre(request.getGenre());
        movie.setDuration(request.getDuration());
        movie.setRating(request.getRating());
        movie.setAbout(request.getAbout());
        movie.setPosterUrl(request.getPosterUrl());
        // no setCreatedAt — @PrePersist handles it

        movieRepository.save(movie);
        return toDTO(movie);
    }

    @Override
    public MovieResponseDTO getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + id));
        return toDTO(movie);
    }

    @Override
    public List<MovieResponseDTO> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MovieResponseDTO updateMovie(Long id, MovieRequestDTO request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + id));

        movie.setTitle(request.getTitle());
        movie.setLanguage(request.getLanguage());
        movie.setDirector(request.getDirector());
        movie.setReleaseYear(request.getReleaseYear());
        movie.setGenre(request.getGenre());
        movie.setDuration(request.getDuration());
        movie.setRating(request.getRating());
        movie.setAbout(request.getAbout());
        if (request.getPosterUrl() != null) {
            movie.setPosterUrl(request.getPosterUrl());
        }

        movieRepository.save(movie);
        return toDTO(movie);
    }

    @Override
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new ResourceNotFoundException("Movie not found: " + id);
        }
        movieRepository.deleteById(id);
    }

    private MovieResponseDTO toDTO(Movie movie) {
        return MovieResponseDTO.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .language(movie.getLanguage())
                .director(movie.getDirector())
                .releaseYear(movie.getReleaseYear())
                .genre(movie.getGenre())
                .duration(movie.getDuration())
                .rating(movie.getRating())
                .about(movie.getAbout())
                .posterUrl(movie.getPosterUrl())
                .createdAt(movie.getCreatedAt())
                .build();
    }
}
