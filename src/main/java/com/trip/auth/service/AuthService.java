package com.trip.auth.service;

import com.trip.auth.dto.AuthToken;
import org.springframework.stereotype.Service;


@Service
public interface AuthService {
    AuthToken loginUser(String email, String password);
    AuthToken refreshAccessToken(String refreshToken);
    void logoutUser(String refreshToken);
}
