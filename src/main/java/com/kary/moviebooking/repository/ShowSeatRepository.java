package com.kary.moviebooking.repository;

import com.kary.moviebooking.entity.ShowSeat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ss FROM ShowSeat ss WHERE ss.show.id = :showId AND ss.seat.id IN :seatIds")
    List<ShowSeat> findWithLock(@Param("showId") Long showId,
                                @Param("seatIds") List<Long> seatIds);

    @Query("SELECT ss FROM ShowSeat ss WHERE ss.seatStatus = 'TEMP_LOCKED' AND ss.lockedAt < :expiryTime")
    List<ShowSeat> findExpiredLocks(@Param("expiryTime") LocalDateTime expiryTime);

    @Lock(LockModeType.PESSIMISTIC_WRITE) //This ensures:No other user can modify those seats during booking Prevents race conditions
    @Query("SELECT ss FROM ShowSeat ss WHERE ss.show.id = :showId AND ss.lockedByUserId = :userId AND ss.seatStatus = 'TEMP_LOCKED'")
    List<ShowSeat> findLockedSeatsByUser(
            @Param("showId") Long showId,
            @Param("userId") Long userId);

    List<ShowSeat> findByShow_IdOrderBySeat_RowNumberAscSeat_SeatNumberAsc(Long showId);

    void deleteByShow_Id(Long showId);
}