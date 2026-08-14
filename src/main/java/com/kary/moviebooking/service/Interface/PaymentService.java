package com.kary.moviebooking.service.Interface;

import com.kary.moviebooking.dto.PaymentVerificationResponseDTO;
import com.kary.moviebooking.dto.RazorpayOrderResponseDTO;
import com.kary.moviebooking.dto.VerifyPaymentRequestDTO;

public interface PaymentService {

    RazorpayOrderResponseDTO createRazorpayOrder(Long bookingId, Double amount);

    PaymentVerificationResponseDTO verifyPayment(VerifyPaymentRequestDTO request, String username);

    void handleWebhookEvent(String payload, String signatureHeader);
}
