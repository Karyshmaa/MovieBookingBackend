package com.kary.moviebooking.service.Impl;

import com.kary.moviebooking.dto.AdminDashboardStatsDTO;
import com.kary.moviebooking.enums.BookingStatus;
import com.kary.moviebooking.repository.*;
import com.kary.moviebooking.service.Interface.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;
    private final ShowRepository showRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public AdminDashboardStatsDTO getDashboardStats() {
        return AdminDashboardStatsDTO.builder()
                .totalMovies(movieRepository.count())
                .totalTheaters(theaterRepository.count())
                .totalScreens(screenRepository.count())
                .totalShows(showRepository.count())
                .totalUsers(userRepository.count())
                .totalBookings(bookingRepository.count())
                .confirmedBookings(bookingRepository.countByStatus(BookingStatus.CONFIRMED))
                .pendingBookings(bookingRepository.countByStatus(BookingStatus.PENDING))
                .cancelledBookings(bookingRepository.countByStatus(BookingStatus.CANCELLED))
                .totalRevenue(bookingRepository.sumRevenueFromConfirmedBookings())
                .build();
    }
}
