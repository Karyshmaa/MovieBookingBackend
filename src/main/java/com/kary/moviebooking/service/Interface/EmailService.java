package com.kary.moviebooking.service.Interface;

public interface EmailService {
    void sendWelcomeEmail(String toEmail, String userName);
    void sendPasswordResetEmail(String toEmail, String userName, String resetLink);
}
