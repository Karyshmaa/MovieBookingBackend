package com.kary.moviebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardStatsDTO {
    private long totalMovies;
    private long totalTheaters;
    private long totalScreens;
    private long totalShows;
    private long totalUsers;
    private long totalBookings;
    private long confirmedBookings;
    private long pendingBookings;
    private long cancelledBookings;
    private double totalRevenue;
}