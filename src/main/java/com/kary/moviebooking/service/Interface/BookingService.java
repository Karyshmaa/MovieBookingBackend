package com.kary.moviebooking.service.Interface;

import com.kary.moviebooking.dto.BookingRequestDTO;
import com.kary.moviebooking.dto.BookingResponseDTO;

import java.util.List;

public interface BookingService {

    BookingResponseDTO initiateBooking(BookingRequestDTO request, String username);

    BookingResponseDTO getBookingById(Long id);

    BookingResponseDTO getBookingByIdForUser(Long id, String username);

    List<BookingResponseDTO> getBookingsForUser(String username);

    List<BookingResponseDTO> getAllBookings();

    void deleteBooking(Long id);

    void cancelBookingForUser(Long id, String username);
}
