package com.kary.moviebooking.service.impl;

import com.kary.moviebooking.dto.ScreenRequestDTO;
import com.kary.moviebooking.dto.ScreenResponseDTO;
import com.kary.moviebooking.entity.Screen;
import com.kary.moviebooking.entity.Theater;
import com.kary.moviebooking.exception.ResourceNotFoundException;
import com.kary.moviebooking.repository.ScreenRepository;
import com.kary.moviebooking.repository.TheaterRepository;
import com.kary.moviebooking.service.Interface.ScreenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScreenServiceImpl implements ScreenService {

    private final ScreenRepository screenRepository;
    private final TheaterRepository theaterRepository;

    @Override
    public ScreenResponseDTO createScreen(ScreenRequestDTO request) {
        Theater theater = theaterRepository.findById(request.getTheaterId())
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found: " + request.getTheaterId()));

        Screen screen = new Screen();
        screen.setName(request.getName());
        screen.setTotalSeats(request.getTotalSeats()); // ✅ was missing!
        screen.setTheater(theater);
        screenRepository.save(screen);
        return toDTO(screen);
    }

    @Override
    public ScreenResponseDTO getScreenById(Long id) {
        Screen screen = screenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found: " + id));
        return toDTO(screen);
    }

    @Override
    public List<ScreenResponseDTO> getAllScreens() {
        return screenRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ScreenResponseDTO> getScreensByTheaterId(Long theaterId) {
        return screenRepository.findByTheaterId(theaterId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteScreen(Long id) {
        if (!screenRepository.existsById(id)) {
            throw new ResourceNotFoundException("Screen not found: " + id);
        }
        screenRepository.deleteById(id);
    }

    private ScreenResponseDTO toDTO(Screen screen) {
        return ScreenResponseDTO.builder()
                .id(screen.getId())
                .name(screen.getName())
                .totalSeats(screen.getTotalSeats()) // ✅ was missing!
                .theaterId(screen.getTheater().getId())
                .theaterName(screen.getTheater().getName())
                .build();
    }
}
