package com.kary.moviebooking.service.Impl;

import com.kary.moviebooking.entity.ShowSeat;
import com.kary.moviebooking.enums.SeatStatus;
import com.kary.moviebooking.repository.ShowSeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeatUnlockScheduler {

    private final ShowSeatRepository showSeatRepository;

    @Scheduled(fixedRate = 300000)   // every 5 mins
    @Transactional
    public void unlockExpiredSeats() {

        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(5);

        List<ShowSeat> expiredSeats = showSeatRepository.findExpiredLocks(expiryTime);

        if (expiredSeats.isEmpty()) return;

        expiredSeats.forEach(seat -> {
            seat.setSeatStatus(SeatStatus.AVAILABLE);
            seat.setLockedAt(null);
            seat.setLockedByUserId(null);
        });

        showSeatRepository.saveAll(expiredSeats);
        System.out.println("Released " + expiredSeats.size() + " expired seat locks");
    }
}