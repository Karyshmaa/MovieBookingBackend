package com.kary.moviebooking.service.Impl;

import com.kary.moviebooking.dto.PaymentVerificationResponseDTO;
import com.kary.moviebooking.dto.RazorpayOrderResponseDTO;
import com.kary.moviebooking.dto.VerifyPaymentRequestDTO;
import com.kary.moviebooking.entity.Booking;
import com.kary.moviebooking.entity.BookingSeat;
import com.kary.moviebooking.entity.Payment;
import com.kary.moviebooking.entity.ShowSeat;
import com.kary.moviebooking.enums.BookingStatus;
import com.kary.moviebooking.enums.PaymentStatus;
import com.kary.moviebooking.enums.SeatStatus;
import com.kary.moviebooking.exception.BadRequestException;
import com.kary.moviebooking.exception.ResourceNotFoundException;
import com.kary.moviebooking.repository.BookingRepository;
import com.kary.moviebooking.repository.BookingSeatRepository;
import com.kary.moviebooking.repository.PaymentRepository;
import com.kary.moviebooking.repository.ShowSeatRepository;
import com.kary.moviebooking.service.Interface.PaymentService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final ShowSeatRepository showSeatRepository;
    private final BookingSeatRepository bookingSeatRepository;

    private final String keyId;
    private final String keySecret;
    private final String webhookSecret;
    private final String currency;

    public PaymentServiceImpl(
            BookingRepository bookingRepository,
            PaymentRepository paymentRepository,
            ShowSeatRepository showSeatRepository,
            BookingSeatRepository bookingSeatRepository,
            @Value("${razorpay.key-id}") String keyId,
            @Value("${razorpay.key-secret}") String keySecret,
            @Value("${razorpay.webhook-secret:}") String webhookSecret,
            @Value("${razorpay.currency:INR}") String currency) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.showSeatRepository = showSeatRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.keyId = keyId;
        this.keySecret = keySecret;
        this.webhookSecret = (webhookSecret == null || webhookSecret.isBlank()) ? keySecret : webhookSecret;
        this.currency = currency;
    }

    @Override
    @Transactional
    public RazorpayOrderResponseDTO createRazorpayOrder(Long bookingId, Double amount) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        try {
            RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);

            // Razorpay expects amount in the smallest currency unit (paise for INR)
            long amountInPaise = Math.round(amount * 100);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", "booking_" + bookingId);
            orderRequest.put("payment_capture", 1);

            com.razorpay.Order order = razorpayClient.orders.create(orderRequest);

            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setRazorpayOrderId(order.get("id"));
            payment.setAmount(amount);
            payment.setCurrency(currency);
            payment.setStatus(PaymentStatus.CREATED);
            paymentRepository.save(payment);

            return RazorpayOrderResponseDTO.builder()
                    .razorpayOrderId(order.get("id"))
                    .razorpayKeyId(keyId)
                    .amount(amount)
                    .currency(currency)
                    .bookingId(bookingId)
                    .build();

        } catch (RazorpayException e) {
            log.error("Failed to create Razorpay order for booking {}: {}", bookingId, e.getMessage());
            throw new BadRequestException("Unable to initiate payment. Please try again.");
        }
    }

    @Override
    @Transactional
    public PaymentVerificationResponseDTO verifyPayment(VerifyPaymentRequestDTO request, String username) {

        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found for this order"));

        Booking booking = payment.getBooking();

        // ownership check: the logged-in user must own this booking
        if (booking.getUser() == null || !booking.getUser().getEmail().equalsIgnoreCase(username)) {
            throw new BadRequestException("This booking does not belong to the current user");
        }

        boolean isValidSignature;
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());

            isValidSignature = Utils.verifyPaymentSignature(options, keySecret);
        } catch (RazorpayException e) {
            log.error("Signature verification error: {}", e.getMessage());
            isValidSignature = false;
        }

        if (!isValidSignature) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Signature verification failed");
            paymentRepository.save(payment);

            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            releaseSeatsForBooking(booking);

            return PaymentVerificationResponseDTO.builder()
                    .success(false)
                    .message("Payment verification failed. If money was deducted, it will be refunded automatically.")
                    .bookingId(booking.getId())
                    .bookingStatus(booking.getStatus().name())
                    .build();
        }

        // payment verified — confirm booking and mark seats as BOOKED
        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setRazorpaySignature(request.getRazorpaySignature());
        payment.setStatus(PaymentStatus.PAID);
        paymentRepository.save(payment);

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        confirmSeatsForBooking(booking);

        return PaymentVerificationResponseDTO.builder()
                .success(true)
                .message("Payment verified successfully. Booking confirmed!")
                .bookingId(booking.getId())
                .bookingStatus(booking.getStatus().name())
                .build();
    }

    @Override
    @Transactional
    public void handleWebhookEvent(String payload, String signatureHeader) {
        try {
            boolean valid = Utils.verifyWebhookSignature(payload, signatureHeader, webhookSecret);
            if (!valid) {
                log.warn("Invalid Razorpay webhook signature received");
                return;
            }
        } catch (RazorpayException e) {
            log.error("Webhook signature verification error: {}", e.getMessage());
            return;
        }

        JSONObject event = new JSONObject(payload);
        String eventType = event.optString("event");

        if ("payment.captured".equals(eventType) || "payment.failed".equals(eventType)) {
            try {
                JSONObject paymentEntity = event
                        .getJSONObject("payload")
                        .getJSONObject("payment")
                        .getJSONObject("entity");

                String orderId = paymentEntity.optString("order_id");
                String paymentId = paymentEntity.optString("id");

                paymentRepository.findByRazorpayOrderId(orderId).ifPresent(payment -> {
                    if ("payment.captured".equals(eventType)
                            && payment.getStatus() != PaymentStatus.PAID) {
                        payment.setRazorpayPaymentId(paymentId);
                        payment.setStatus(PaymentStatus.PAID);
                        paymentRepository.save(payment);

                        Booking booking = payment.getBooking();
                        booking.setStatus(BookingStatus.CONFIRMED);
                        bookingRepository.save(booking);
                        confirmSeatsForBooking(booking);

                    } else if ("payment.failed".equals(eventType)
                            && payment.getStatus() == PaymentStatus.CREATED) {
                        payment.setStatus(PaymentStatus.FAILED);
                        payment.setFailureReason("Payment failed (webhook)");
                        paymentRepository.save(payment);

                        Booking booking = payment.getBooking();
                        booking.setStatus(BookingStatus.CANCELLED);
                        bookingRepository.save(booking);
                        releaseSeatsForBooking(booking);
                    }
                });
            } catch (Exception e) {
                log.error("Error processing Razorpay webhook payload: {}", e.getMessage());
            }
        }
    }

    private void confirmSeatsForBooking(Booking booking) {
        List<BookingSeat> bookingSeats = bookingSeatRepository.findByBooking_Id(booking.getId());
        List<ShowSeat> seats = bookingSeats.stream().map(BookingSeat::getShowSeat).toList();
        seats.forEach(seat -> {
            seat.setSeatStatus(SeatStatus.BOOKED);
            seat.setBooking(booking);
        });
        showSeatRepository.saveAll(seats);
    }

    private void releaseSeatsForBooking(Booking booking) {
        List<BookingSeat> bookingSeats = bookingSeatRepository.findByBooking_Id(booking.getId());
        List<ShowSeat> seats = bookingSeats.stream().map(BookingSeat::getShowSeat).toList();
        seats.forEach(seat -> {
            seat.setSeatStatus(SeatStatus.AVAILABLE);
            seat.setLockedAt(null);
            seat.setLockedByUserId(null);
        });
        showSeatRepository.saveAll(seats);
    }
}

