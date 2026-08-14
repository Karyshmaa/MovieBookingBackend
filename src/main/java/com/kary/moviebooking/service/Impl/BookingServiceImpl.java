package com.kary.moviebooking.service.Impl;

import com.kary.moviebooking.dto.BookingRequestDTO;
import com.kary.moviebooking.dto.BookingResponseDTO;
import com.kary.moviebooking.dto.RazorpayOrderResponseDTO;
import com.kary.moviebooking.entity.*;
import com.kary.moviebooking.enums.BookingStatus;
import com.kary.moviebooking.enums.SeatStatus;
import com.kary.moviebooking.exception.BadRequestException;
import com.kary.moviebooking.exception.ResourceNotFoundException;
import com.kary.moviebooking.exception.SeatNotAvailableException;
import com.kary.moviebooking.repository.*;
import com.kary.moviebooking.service.Interface.BookingService;
import com.kary.moviebooking.service.Interface.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final int LOCK_DURATION_MINUTES = 5;

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;

    @Override
    public BookingResponseDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
        return toDTO(booking, null);
    }

    @Override
    public BookingResponseDTO getBookingByIdForUser(Long id, String username) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isOwner = booking.getUser() != null && booking.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() != null && user.getRole().name().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new BadRequestException("You are not allowed to view this booking");
        }

        return toDTO(booking, null);
    }

    @Override
    public List<BookingResponseDTO> getBookingsForUser(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return bookingRepository.findByUser_IdOrderByBookedAtDesc(user.getId())
                .stream()
                .map(b -> toDTO(b, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(b -> toDTO(b, null))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Booking not found: " + id);
        }
        bookingRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void cancelBookingForUser(Long id, String username) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isOwner = booking.getUser() != null && booking.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() != null && user.getRole().name().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new BadRequestException("You are not allowed to cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // release the seats back to AVAILABLE so others can book them
        List<BookingSeat> bookingSeats = bookingSeatRepository.findByBooking_Id(booking.getId());
        List<ShowSeat> seats = bookingSeats.stream().map(BookingSeat::getShowSeat).toList();
        seats.forEach(seat -> {
            seat.setSeatStatus(SeatStatus.AVAILABLE);
            seat.setLockedAt(null);
            seat.setLockedByUserId(null);
            seat.setBooking(null);
        });
        showSeatRepository.saveAll(seats);
    }

    @Override
    @Transactional
    public BookingResponseDTO initiateBooking(BookingRequestDTO request, String username) {

        // 1. Fetch user from JWT username
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2. Fetch show
        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        // 3. Fetch and validate requested seats
        List<ShowSeat> seats = showSeatRepository.findAllById(request.getSeatIds());

        if (seats.size() != request.getSeatIds().size()) {
            throw new ResourceNotFoundException("One or more seats not found");
        }

        // make sure all requested seats actually belong to this show
        boolean allBelongToShow = seats.stream()
                .allMatch(s -> s.getShow().getId().equals(show.getId()));
        if (!allBelongToShow) {
            throw new BadRequestException("One or more seats do not belong to the selected show");
        }

        // 4. Check all seats are usable — AVAILABLE, or TEMP_LOCKED by this same user, or an expired lock
        LocalDateTime now = LocalDateTime.now();
        seats.forEach(seat -> {
            boolean isAvailable = seat.getSeatStatus() == SeatStatus.AVAILABLE;
            boolean isOwnExpiredOrActiveLock = seat.getSeatStatus() == SeatStatus.TEMP_LOCKED
                    && seat.getLockedByUserId() != null
                    && seat.getLockedByUserId().equals(user.getId());
            boolean isExpiredLock = seat.getSeatStatus() == SeatStatus.TEMP_LOCKED
                    && seat.getLockedAt() != null
                    && seat.getLockedAt().plusMinutes(LOCK_DURATION_MINUTES).isBefore(now);

            if (!(isAvailable || isOwnExpiredOrActiveLock || isExpiredLock)) {
                throw new SeatNotAvailableException(
                        "Seat " + seat.getId() + " is not available"
                );
            }
        });

        // 5. Lock all seats as TEMP_LOCKED for this user
        seats.forEach(seat -> {
            seat.setSeatStatus(SeatStatus.TEMP_LOCKED);
            seat.setLockedAt(now);
            seat.setLockedByUserId(user.getId());
        });
        showSeatRepository.saveAll(seats);

        // 6. Calculate total amount
        double total = seats.stream()
                .mapToDouble(ShowSeat::getPrice)
                .sum();

        // 7. Create PENDING booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setStatus(BookingStatus.PENDING);          // ✅ PENDING, not confirmed yet
        booking.setTotalAmount(total);
        booking.setBookedAt(now);
        booking = bookingRepository.save(booking);

        // 8. Save BookingSeats join records
        Booking finalBooking = booking;
        List<BookingSeat> bookingSeats = seats.stream().map(seat -> {
            BookingSeat bs = new BookingSeat();
            bs.setBooking(finalBooking);
            bs.setShowSeat(seat);
            bs.setBookedAt(now);
            return bs;
        }).collect(Collectors.toList());
        bookingSeatRepository.saveAll(bookingSeats);

        // 9. Create Razorpay order
        RazorpayOrderResponseDTO order = paymentService.createRazorpayOrder(booking.getId(), total);

        // 10. Build and return response
        return toDTO(booking, order);
    }

    private BookingResponseDTO toDTO(Booking booking, RazorpayOrderResponseDTO order) {
        List<BookingSeat> bookingSeats = bookingSeatRepository.findByBooking_Id(booking.getId());

        List<Long> seatIds = bookingSeats.stream()
                .map(bs -> bs.getShowSeat().getId())
                .collect(Collectors.toList());

        List<String> seatLabels = bookingSeats.stream()
                .map(bs -> bs.getShowSeat().getSeat().getRowNumber() + bs.getShowSeat().getSeat().getSeatNumber())
                .collect(Collectors.toList());

        Show show = booking.getShow();

        return BookingResponseDTO.builder()
                .bookingId(booking.getId())
                .showId(show != null ? show.getId() : null)
                .userId(booking.getUser() != null ? booking.getUser().getId() : null)
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus().name())
                .razorpayOrderId(order != null ? order.getRazorpayOrderId() : null)
                .razorpayKeyId(order != null ? order.getRazorpayKeyId() : null)
                .lockedSeatIds(seatIds)
                .bookedAt(booking.getBookedAt())
                .movieTitle(show != null && show.getMovie() != null ? show.getMovie().getTitle() : null)
                .moviePosterUrl(show != null && show.getMovie() != null ? show.getMovie().getPosterUrl() : null)
                .theaterName(show != null && show.getTheater() != null ? show.getTheater().getName() : null)
                .screenName(show != null && show.getScreen() != null ? show.getScreen().getName() : null)
                .showTime(show != null ? show.getShowTime() : null)
                .seatLabels(seatLabels)
                .build();
    }
}
