package com.kary.moviebooking.service.Impl;

import com.kary.moviebooking.entity.User;
import com.kary.moviebooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + username));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}

//eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE3Nzc0Njk4NjMsImV4cCI6MTc3NzQ3MzQ2M30.DVJofE1PW1DVtSbmciqJxJfmvk_5H0CKVLDNXqNBeZ0 admin
//eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJrYXJ5QGdtYWlsLmNvbSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzc3NDcwNDg0LCJleHAiOjE3Nzc0NzQwODR9.cmbtCDG1KvvgkIUKUKRVbp8BIgKjCmzBadfoKbd_jV8 user