package com.kary.moviebooking.controller;

import com.kary.moviebooking.dto.*;
import com.kary.moviebooking.service.Interface.Authservice;
import com.kary.moviebooking.service.Interface.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final Authservice authservice;
    private final PasswordResetService passwordResetService;

    // calls authservice
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody @Valid SignupRequestDTO request) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(authservice.signup(request));
    }

    //email verification
    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token) {
        return ResponseEntity.ok(authservice.verify(token));
    }

    //returns token + role + email
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody @Valid LoginRequestDTO request) {
        return ResponseEntity.ok(authservice.login(request));   // ✅ clean
    }

    //forgot password
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequestDTO request) {
        passwordResetService.forgotPassword(request);
        return ResponseEntity.ok("If this email is registered, a reset link has been sent");
    }

    // reset password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody @Valid ResetPasswordRequestDTO request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok("Password reset successful. You can now log in");
    }
}
