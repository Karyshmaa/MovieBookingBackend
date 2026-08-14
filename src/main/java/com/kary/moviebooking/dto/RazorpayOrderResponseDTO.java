package com.kary.moviebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RazorpayOrderResponseDTO {
    private String razorpayOrderId;
    private String razorpayKeyId;   // public key id, frontend needs this to open checkout
    private Double amount;          // in rupees
    private String currency;
    private Long bookingId;
}
