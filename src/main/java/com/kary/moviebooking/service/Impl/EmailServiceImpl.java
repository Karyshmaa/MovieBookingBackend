package com.kary.moviebooking.service.impl;

import com.kary.moviebooking.service.Interface.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final String appName;
    private final String frontendUrl;

    public EmailServiceImpl(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String fromEmail,
            @Value("${app.name}") String appName,
            @Value("${app.frontend.url:http://localhost:5173}") String frontendUrl) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.appName = appName;
        this.frontendUrl = frontendUrl;
    }

    @Override
    @Async   // ✅ sends email in background — user doesn't wait for it
    public void sendWelcomeEmail(String toEmail, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Welcome to " + appName + "! 🎬");
            helper.setText(buildWelcomeEmailBody(userName), true); // true = HTML

            mailSender.send(message);
            log.info("Welcome email sent to {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
            // ✅ we catch and log — don't let email failure break registration
        }
    }

    private String buildWelcomeEmailBody(String userName) {
        return "<html>"
                + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 30px;'>"
                + "<div style='max-width: 600px; margin: auto; background: white;"
                + "border-radius: 10px; padding: 40px;'>"

                + "<h1 style='color: #e63946;'>🎬 Welcome to MovieBooking!</h1>"

                + "<p style='font-size: 16px; color: #333;'>"
                + "Hi <strong>" + userName + "</strong>,"
                + "</p>"

                + "<p style='font-size: 15px; color: #555;'>"
                + "We're thrilled to have you on board! You can now:"
                + "</p>"

                + "<ul style='font-size: 15px; color: #555; line-height: 2;'>"
                + "<li>🎥 Browse the latest movies</li>"
                + "<li>🪑 Book seats in seconds</li>"
                + "<li>💳 Pay securely via Razorpay</li>"
                + "<li>📧 Get instant booking confirmations</li>"
                + "</ul>"

                + "<div style='margin-top: 30px; padding: 15px; background: #e63946;"
                + "border-radius: 8px; text-align: center;'>"
                + "<a href='" + frontendUrl + "' style='color: white;"
                + "font-size: 16px; text-decoration: none; font-weight: bold;'>"
                + "Start Booking Now →"
                + "</a>"
                + "</div>"

                + "<p style='margin-top: 30px; font-size: 13px; color: #aaa;'>"
                + "If you didn't create this account, please ignore this email."
                + "</p>"

                + "</div>"
                + "</body>"
                + "</html>";
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String toEmail, String userName, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Reset Your Password 🔐");
            helper.setText(buildResetEmailBody(userName, resetLink), true);

            mailSender.send(message);
            log.info("Password reset email sent to {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send reset email to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildResetEmailBody(String userName, String resetLink) {
        return "<html>"
                + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 30px;'>"
                + "<div style='max-width: 600px; margin: auto; background: white;"
                + "border-radius: 10px; padding: 40px;'>"

                + "<h1 style='color: #e63946;'>🔐 Password Reset</h1>"

                + "<p style='font-size: 16px; color: #333;'>"
                + "Hi <strong>" + userName + "</strong>,"
                + "</p>"

                + "<p style='font-size: 15px; color: #555;'>"
                + "We received a request to reset your password. "
                + "Click the button below — this link expires in <strong>15 minutes</strong>."
                + "</p>"

                + "<div style='margin-top: 30px; padding: 15px; background: #e63946;"
                + "border-radius: 8px; text-align: center;'>"
                + "<a href='" + resetLink + "' style='color: white;"
                + "font-size: 16px; text-decoration: none; font-weight: bold;'>"
                + "Reset My Password →"
                + "</a>"
                + "</div>"

                + "<p style='margin-top: 20px; font-size: 13px; color: #aaa;'>"
                + "If you didn't request this, ignore this email. "
                + "Your password will not change."
                + "</p>"

                + "</div>"
                + "</body>"
                + "</html>";
    }
}
