package com.kary.moviebooking.service.impl;

import com.kary.moviebooking.dto.ShowSeatResponseDTO;
import com.kary.moviebooking.entity.Seat;
import com.kary.moviebooking.entity.Show;
import com.kary.moviebooking.entity.ShowSeat;
import com.kary.moviebooking.entity.User;
import com.kary.moviebooking.enums.SeatStatus;
import com.kary.moviebooking.exception.ResourceNotFoundException;
import com.kary.moviebooking.exception.SeatNotAvailableException;
import com.kary.moviebooking.repository.SeatRepository;
import com.kary.moviebooking.repository.ShowSeatRepository;
import com.kary.moviebooking.repository.UserRepository;
import com.kary.moviebooking.service.Interface.ShowSeatService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShowSeatServiceImpl implements ShowSeatService {

    private static final int LOCK_DURATION_MINUTES = 5;

    private final ShowSeatRepository showSeatRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void createShowSeats(Show show) {
        // ✅ use findByScreen_Id (consistent with fixed SeatRepository)
        List<Seat> seats = seatRepository.findByScreen_Id(show.getScreen().getId());

        if (seats.isEmpty()) {
            log.warn("No seats found for screen {} — ShowSeats not created for show {}. " +
                            "Define seat layout first, then create the show.",
                    show.getScreen().getId(), show.getId());
            return;
        }

        List<ShowSeat> showSeats = seats.stream().map(seat -> {
            ShowSeat ss = new ShowSeat();
            ss.setShow(show);
            ss.setSeat(seat);
            ss.setSeatStatus(SeatStatus.AVAILABLE);
            ss.setPrice(getDefaultPrice(seat));
            return ss;
        }).collect(Collectors.toList());

        showSeatRepository.saveAll(showSeats);
        log.info("Created {} ShowSeats for show {}", showSeats.size(), show.getId());
    }

    @Override
    @Transactional
    public void deleteShowSeatsByShowId(Long showId) {
        showSeatRepository.deleteByShow_Id(showId);
        log.info("Deleted ShowSeats for show {}", showId);
    }

    @Override
    @Transactional
    public void lockSeatsForUser(List<Long> seatIds, Long showId, String username) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<ShowSeat> seats = showSeatRepository.findWithLock(showId, seatIds);

        if (seats.size() != seatIds.size()) {
            throw new SeatNotAvailableException("One or more seats not found for this show");
        }

        LocalDateTime now = LocalDateTime.now();

        for (ShowSeat seat : seats) {
            if (seat.getSeatStatus() == SeatStatus.BOOKED) {
                throw new SeatNotAvailableException("Seat " + seat.getId() + " is already booked");
            }
            if (seat.getSeatStatus() == SeatStatus.TEMP_LOCKED) {
                boolean lockedByMe = seat.getLockedByUserId() != null
                        && seat.getLockedByUserId().equals(user.getId());
                boolean lockStillActive = seat.getLockedAt() != null
                        && seat.getLockedAt().plusMinutes(LOCK_DURATION_MINUTES).isAfter(now);

                if (!lockedByMe && lockStillActive) {
                    throw new SeatNotAvailableException("Seat " + seat.getId() + " is locked by another user");
                }
            }
            seat.setSeatStatus(SeatStatus.TEMP_LOCKED);
            seat.setLockedByUserId(user.getId());
            seat.setLockedAt(now);
        }

        showSeatRepository.saveAll(seats);
    }

    @Override
    public List<ShowSeatResponseDTO> getSeatMapForShow(Long showId) {
        List<ShowSeat> showSeats = showSeatRepository
                .findByShow_IdOrderBySeat_RowNumberAscSeat_SeatNumberAsc(showId);

        LocalDateTime now = LocalDateTime.now();

        return showSeats.stream().map(ss -> {
            String effectiveStatus = ss.getSeatStatus().name();
            if (ss.getSeatStatus() == SeatStatus.TEMP_LOCKED
                    && ss.getLockedAt() != null
                    && ss.getLockedAt().plusMinutes(LOCK_DURATION_MINUTES).isBefore(now)) {
                effectiveStatus = SeatStatus.AVAILABLE.name();
            }

            Seat seat = ss.getSeat();
            return ShowSeatResponseDTO.builder()
                    .showSeatId(ss.getId())
                    .rowNumber(seat.getRowNumber())
                    .seatNumber(seat.getSeatNumber())
                    .seatLabel(seat.getRowNumber() + seat.getSeatNumber())
                    .seatType(seat.getSeatType().name())
                    .seatStatus(effectiveStatus)
                    .price(ss.getPrice())
                    .build();
        }).collect(Collectors.toList());
    }

    private Double getDefaultPrice(Seat seat) {
        return switch (seat.getSeatType()) {
            case PREMIUM -> 350.0;
            case RECLINER -> 500.0;
            default -> 250.0;
        };
    }
}
