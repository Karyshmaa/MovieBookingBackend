package com.kary.moviebooking.service.Impl;

import com.kary.moviebooking.entity.User;
import com.kary.moviebooking.entity.VerificationToken;
import com.kary.moviebooking.repository.VerificationTokenRepository;
import com.kary.moviebooking.service.Interface.VerificationTokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository repository;

    public VerificationTokenServiceImpl(VerificationTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public VerificationToken createToken(User user) {

        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        return repository.save(token);
    }

    @Override
    public VerificationToken getByToken(String token) {
        return repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
    }

    @Override
    public void deleteToken(VerificationToken token) {
        repository.delete(token);
    }
}