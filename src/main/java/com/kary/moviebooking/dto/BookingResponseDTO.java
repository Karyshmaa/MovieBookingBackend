package com.kary.moviebooking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BookingResponseDTO {
    private Long bookingId;
    private Long showId;
    private Long userId;
    private Double totalAmount;
    private String status;
    private String razorpayOrderId;        // frontend uses this to open Razorpay checkout
    private String razorpayKeyId;          // public key id for Razorpay checkout widget
    private List<Long> lockedSeatIds;
    private LocalDateTime bookedAt;

    // ✅ extra display info so the frontend doesn't need extra calls
    private String movieTitle;
    private String moviePosterUrl;
    private String theaterName;
    private String screenName;
    private LocalDateTime showTime;
    private List<String> seatLabels;       // e.g. ["A1", "A2"]
}