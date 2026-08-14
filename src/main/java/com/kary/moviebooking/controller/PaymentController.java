package com.kary.moviebooking.controller;

import com.kary.moviebooking.dto.PaymentVerificationResponseDTO;
import com.kary.moviebooking.dto.VerifyPaymentRequestDTO;
import com.kary.moviebooking.service.Interface.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Verify the payment signature returned by Razorpay checkout on the frontend
     * after the user completes payment. On success, the booking is CONFIRMED
     * and seats are marked BOOKED.
     */
    @PostMapping("/verify")
    public ResponseEntity<PaymentVerificationResponseDTO> verifyPayment(
            @RequestBody @Valid VerifyPaymentRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        PaymentVerificationResponseDTO response =
                paymentService.verifyPayment(request, userDetails.getUsername());

        return ResponseEntity.ok(response);
    }

    /**
     * Razorpay server-to-server webhook. Configure this URL in the Razorpay
     * Dashboard -> Settings -> Webhooks, subscribed to payment.captured and
     * payment.failed events. This is a safety net in case the frontend
     * never calls /verify (e.g. user closes the tab right after paying).
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> razorpayWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {

        paymentService.handleWebhookEvent(payload, signature);
        return ResponseEntity.ok("ok");
    }
}
