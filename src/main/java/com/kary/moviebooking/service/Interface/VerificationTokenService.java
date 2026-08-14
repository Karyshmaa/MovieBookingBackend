package com.kary.moviebooking.service.Interface;

import com.kary.moviebooking.entity.User;
import com.kary.moviebooking.entity.VerificationToken;

public interface VerificationTokenService {

    VerificationToken createToken(User user);

    VerificationToken getByToken(String token);

    void deleteToken(VerificationToken token);
}