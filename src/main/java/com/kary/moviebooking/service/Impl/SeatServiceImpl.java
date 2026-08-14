package com.kary.moviebooking.service.Impl;

import com.kary.moviebooking.dto.SeatLayoutRequestDTO;
import com.kary.moviebooking.dto.SeatResponseDTO;
import com.kary.moviebooking.entity.Screen;
import com.kary.moviebooking.entity.Seat;
import com.kary.moviebooking.entity.Show;
import com.kary.moviebooking.entity.ShowSeat;
import com.kary.moviebooking.enums.SeatStatus;
import com.kary.moviebooking.enums.SeatType;
import com.kary.moviebooking.exception.BadRequestException;
import com.kary.moviebooking.exception.ResourceNotFoundException;
import com.kary.moviebooking.repository.ScreenRepository;
import com.kary.moviebooking.repository.SeatRepository;
import com.kary.moviebooking.repository.ShowRepository;
import com.kary.moviebooking.repository.ShowSeatRepository;
import com.kary.moviebooking.service.Interface.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final ScreenRepository screenRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;

    @Override
    @Transactional
    public List<SeatResponseDTO> createSeatLayout(Long screenId, SeatLayoutRequestDTO request) {

        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found: " + screenId));

        // 1. Create physical seats for this screen
        List<Seat> seatsToCreate = new ArrayList<>();
        for (SeatLayoutRequestDTO.RowConfig row : request.getRows()) {
            SeatType seatType;
            try {
                seatType = SeatType.valueOf(row.getSeatType().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid seat type: " + row.getSeatType()
                        + ". Must be NORMAL, PREMIUM, or RECLINER");
            }
            for (int seatNum = 1; seatNum <= row.getSeatCount(); seatNum++) {
                Seat seat = new Seat();
                seat.setRowNumber(row.getRowNumber());
                seat.setSeatNumber(seatNum);
                seat.setSeatType(seatType);
                seat.setScreen(screen);
                seatsToCreate.add(seat);
            }
        }

        List<Seat> savedSeats = seatRepository.saveAll(seatsToCreate);
        log.info("Created {} seats for screen {}", savedSeats.size(), screenId);

        // ✅ 2. For every show already scheduled on this screen,
        //       create ShowSeats so booking page works immediately.
        //       This handles the case where show was created BEFORE seat layout.
        List<Show> existingShows = showRepository.findByScreen_Id(screenId);

        if (!existingShows.isEmpty()) {
            log.info("{} existing shows found for screen {} — creating ShowSeats",
                    existingShows.size(), screenId);

            List<ShowSeat> allShowSeatsToCreate = new ArrayList<>();

            for (Show show : existingShows) {
                // ✅ check if ShowSeats already exist for this show (avoid duplicates)
                List<ShowSeat> existing = showSeatRepository
                        .findByShow_IdOrderBySeat_RowNumberAscSeat_SeatNumberAsc(show.getId());

                if (existing.isEmpty()) {
                    // no ShowSeats yet — create them now
                    for (Seat seat : savedSeats) {
                        ShowSeat ss = new ShowSeat();
                        ss.setShow(show);
                        ss.setSeat(seat);
                        ss.setSeatStatus(SeatStatus.AVAILABLE);
                        ss.setPrice(getDefaultPrice(seat));
                        allShowSeatsToCreate.add(ss);
                    }
                    log.info("Queued {} ShowSeats for show {}", savedSeats.size(), show.getId());
                } else {
                    log.info("Show {} already has {} ShowSeats — skipping", show.getId(), existing.size());
                }
            }

            if (!allShowSeatsToCreate.isEmpty()) {
                showSeatRepository.saveAll(allShowSeatsToCreate);
                log.info("Saved {} total ShowSeats for {} shows", allShowSeatsToCreate.size(), existingShows.size());
            }
        }

        return savedSeats.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<SeatResponseDTO> getSeatsByScreenId(Long screenId) {
        return seatRepository.findByScreen_Id(screenId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private Double getDefaultPrice(Seat seat) {
        return switch (seat.getSeatType()) {
            case PREMIUM -> 350.0;
            case RECLINER -> 500.0;
            default -> 250.0;
        };
    }

    private SeatResponseDTO toDTO(Seat seat) {
        return SeatResponseDTO.builder()
                .id(seat.getId())
                .rowNumber(seat.getRowNumber())
                .seatNumber(seat.getSeatNumber())
                .seatLabel(seat.getRowNumber() + seat.getSeatNumber())
                .seatType(seat.getSeatType().name())
                .screenId(seat.getScreen().getId())
                .build();
    }
}
