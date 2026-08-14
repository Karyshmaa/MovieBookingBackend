package com.kary.moviebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVerificationResponseDTO {
    private boolean success;
    private String message;
    private Long bookingId;
    private String bookingStatus;
}
