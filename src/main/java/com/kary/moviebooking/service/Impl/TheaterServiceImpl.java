package com.kary.moviebooking.service.Impl;

import com.kary.moviebooking.dto.TheaterRequestDTO;
import com.kary.moviebooking.dto.TheaterResponseDTO;
import com.kary.moviebooking.entity.Theater;
import com.kary.moviebooking.exception.ResourceNotFoundException;
import com.kary.moviebooking.repository.ScreenRepository;
import com.kary.moviebooking.repository.TheaterRepository;
import com.kary.moviebooking.service.Interface.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TheaterServiceImpl implements TheaterService {
    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;

    @Override
    public TheaterResponseDTO createTheater(TheaterRequestDTO request) {
        Theater theater = new Theater();
        theater.setName(request.getName());
        theater.setLocation(request.getLocation());
        theaterRepository.save(theater);
        return toDTO(theater);
    }

    @Override
    public TheaterResponseDTO getTheaterById(Long id) {
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found: " + id));
        return toDTO(theater);
    }

    @Override
    public List<TheaterResponseDTO> getAllTheaters() {
        return theaterRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTheater(Long id) {
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found: " + id));

        boolean hasScreens = screenRepository.existsByTheaterId(id);
        if (hasScreens) {
            throw new IllegalStateException("Cannot delete theater with active screens. Remove screens first.");
        }

        theaterRepository.delete(theater);
    }

    private TheaterResponseDTO toDTO(Theater theater) {
        return TheaterResponseDTO.builder()
                .id(theater.getId())
                .name(theater.getName())
                .location(theater.getLocation())
                .build();
    }
}
