package com.kary.moviebooking.service.Interface;

import com.kary.moviebooking.dto.LoginRequestDTO;
import com.kary.moviebooking.dto.LoginResponseDTO;
import com.kary.moviebooking.dto.SignupRequestDTO;

public interface Authservice {

    String signup(SignupRequestDTO request);
    String verify(String token);
    LoginResponseDTO login(LoginRequestDTO request);
}
