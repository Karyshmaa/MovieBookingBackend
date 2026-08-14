package com.kary.moviebooking.service.impl;

import com.kary.moviebooking.dto.ForgotPasswordRequestDTO;
import com.kary.moviebooking.dto.ResetPasswordRequestDTO;
import com.kary.moviebooking.entity.PasswordResetToken;
import com.kary.moviebooking.entity.User;
import com.kary.moviebooking.exception.BadRequestException;
import com.kary.moviebooking.repository.PasswordResetTokenRepository;
import com.kary.moviebooking.repository.UserRepository;
import com.kary.moviebooking.service.Interface.EmailService;
import com.kary.moviebooking.service.Interface.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService{
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequestDTO request) {

        // 1. Find user — don't reveal if email exists or not (security best practice)
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        // ✅ even if user not found, return same response
        // this prevents email enumeration attacks
        if (user == null) {
            log.warn("Password reset requested for unknown email: {}", request.getEmail());
            return;
        }

        // 2. Delete any existing tokens for this user
        tokenRepository.deleteByUser_Id(user.getId());

        // 3. Generate a unique token
        String token = UUID.randomUUID().toString();

        // 4. Save token with 15 min expiry
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        resetToken.setUsed(false);
        tokenRepository.save(resetToken);

        // 5. Send email
        String resetLink = frontendUrl + "/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), resetLink);

        log.info("Password reset token generated for user: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDTO request) {

        // 1. Find token
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        // 2. Check if already used
        if (resetToken.isUsed()) {
            throw new BadRequestException("Reset token has already been used");
        }

        // 3. Check expiry
        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new BadRequestException("Reset token has expired. Please request a new one");
        }

        // 4. Update password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // 5. Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Password reset successful for user: {}", user.getEmail());
    }
}

