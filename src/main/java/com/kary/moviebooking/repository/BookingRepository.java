package com.kary.moviebooking.repository;

import com.kary.moviebooking.entity.Booking;
import com.kary.moviebooking.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser_IdOrderByBookedAtDesc(Long userId);

    boolean existsByShow_Id(Long showId);

    long countByStatus(BookingStatus status);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE b.status = 'CONFIRMED'")
    double sumRevenueFromConfirmedBookings();
}
