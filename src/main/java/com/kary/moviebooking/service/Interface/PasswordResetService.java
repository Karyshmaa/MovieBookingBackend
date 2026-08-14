package com.kary.moviebooking.service.Interface;

import com.kary.moviebooking.dto.ForgotPasswordRequestDTO;
import com.kary.moviebooking.dto.ResetPasswordRequestDTO;

public interface PasswordResetService {
    void forgotPassword(ForgotPasswordRequestDTO request);
    void resetPassword(ResetPasswordRequestDTO request);
}
