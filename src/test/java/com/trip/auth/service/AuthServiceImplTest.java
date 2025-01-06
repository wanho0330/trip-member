package com.trip.auth.service;

import com.trip.auth.JwtTokenProvider;
import com.trip.auth.dto.AuthToken;
import com.trip.auth.entity.TokenEntity;
import com.trip.auth.exception.ExpiredTokenException;
import com.trip.auth.repository.AuthRepository;
import com.trip.kafka.KafkaEvent;
import com.trip.kafka.KafkaProducer;
import com.trip.user.entity.UserEntity;
import com.trip.user.exception.UserNotFoundException;
import com.trip.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private AuthRepository authRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void loginUser_Success() {
        // Given
        String email = "test@test.com";
        String password = "password";

        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .password(password)
                .build();

        // idx 삽입이 불가능함
        ReflectionTestUtils.setField(userEntity, "idx", 1L);

        TokenEntity tokenEntity = TokenEntity.builder()
                .token("refresh-token")
                .idx("1")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("refresh-token");
        when(authRepository.save(Mockito.any(TokenEntity.class))).thenReturn(tokenEntity);



        // When
        AuthToken authToken = authService.loginUser(email, password);

        // Then
        assertNotNull(authToken);
        assertEquals("access-token", authToken.getAccessToken());
        assertEquals("refresh-token", authToken.getRefreshToken());
        verify(kafkaProducer, times(1)).sendUserEvent(Mockito.any(KafkaEvent.class));
    }

    @Test
    void loginUser_WrongPassword_ThrowsException() {
        // Given
        String email = "test@test.com";
        String password = "wrong-password";

        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .password("correct-password")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        // When & Then
        assertThrows(UserNotFoundException.class, () -> authService.loginUser(email, password));
        verify(kafkaProducer, never()).sendUserEvent(Mockito.any(KafkaEvent.class));
    }

    @Test
    void loginUser_UserNotFound_ThrowsException() {
        // Given
        String email = "nonexistent@test.com";
        String password = "password";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> authService.loginUser(email, password));
        verify(kafkaProducer, never()).sendUserEvent(Mockito.any(KafkaEvent.class));
    }

    @Test
    void refreshAccessToken_Success() {
        // Given
        String refreshToken = "refresh-token";
        Long userIdx = 1L;

        TokenEntity tokenEntity = TokenEntity.builder()
                .token(refreshToken)
                .idx(userIdx.toString())
                .build();

        UserEntity userEntity = UserEntity.builder()
                .email("test@test.com")
                .build();

        when(authRepository.existsByRefreshToken(refreshToken)).thenReturn(true);
        when(authRepository.findByRefreshToken(refreshToken)).thenReturn(tokenEntity);
        when(jwtTokenProvider.generateAccessToken(userIdx)).thenReturn("new-access-token");
        when(userRepository.findByIdx(userIdx)).thenReturn(Optional.of(userEntity));

        // When
        AuthToken authToken = authService.refreshAccessToken(refreshToken);

        // Then
        assertNotNull(authToken);
        assertEquals("new-access-token", authToken.getAccessToken());
        verify(kafkaProducer, times(1)).sendUserEvent(Mockito.any(KafkaEvent.class));
    }

    @Test
    void refreshAccessToken_ExpiredToken_ThrowsException() {
        // Given
        String refreshToken = "expired-token";

        when(authRepository.existsByRefreshToken(refreshToken)).thenReturn(false);

        // When & Then
        assertThrows(ExpiredTokenException.class, () -> authService.refreshAccessToken(refreshToken));
        verify(kafkaProducer, never()).sendUserEvent(Mockito.any(KafkaEvent.class));
    }

    @Test
    void logoutUser_Success() {
        // Given
        String refreshToken = "refresh-token";
        Long userIdx = 1L;

        TokenEntity tokenEntity = TokenEntity.builder()
                .token(refreshToken)
                .idx(userIdx.toString())
                .build();

        UserEntity userEntity = UserEntity.builder()
                .email("test@test.com")
                .build();

        when(authRepository.existsByRefreshToken(refreshToken)).thenReturn(true);
        when(authRepository.findByRefreshToken(refreshToken)).thenReturn(tokenEntity);
        when(userRepository.findByIdx(userIdx)).thenReturn(Optional.of(userEntity));

        // When
        authService.logoutUser(refreshToken);

        // Then
        verify(authRepository, times(1)).delete(tokenEntity);
        verify(kafkaProducer, times(1)).sendUserEvent(Mockito.any(KafkaEvent.class));
    }

    @Test
    void logoutUser_ExpiredToken_ThrowsException() {
        // Given
        String refreshToken = "expired-token";

        when(authRepository.existsByRefreshToken(refreshToken)).thenReturn(false);

        // When & Then
        assertThrows(ExpiredTokenException.class, () -> authService.logoutUser(refreshToken));
        verify(kafkaProducer, never()).sendUserEvent(Mockito.any(KafkaEvent.class));
    }
}