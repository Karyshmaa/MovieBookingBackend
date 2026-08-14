package com.kary.moviebooking.repository;

import com.kary.moviebooking.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    // delete all tokens for a user when they reset password
    void deleteByUser_Id(Long userId);
}
