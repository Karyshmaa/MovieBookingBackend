package com.kary.moviebooking.service.Interface;

import com.kary.moviebooking.dto.UserRequestDTO;
import com.kary.moviebooking.dto.UserResponseDTO;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO request);
    UserResponseDTO getUserById(Long id);
    UserResponseDTO getUserByEmail(String email);
    Page<UserResponseDTO> getAllUsers(Pageable pageable);
}
