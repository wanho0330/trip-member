package com.trip.user.service;

import com.trip.kafka.KafkaEvent;
import com.trip.kafka.KafkaProducer;
import com.trip.kafka.code.KafkaActions;
import com.trip.user.code.Status;
import com.trip.user.dto.User;
import com.trip.user.entity.UserEntity;
import com.trip.user.exception.EmailAlreadyExistsException;
import com.trip.user.exception.IncorrectPasswordException;
import com.trip.user.exception.UserNotFoundException;
import com.trip.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void createUser_Success() {
        // given
        String email = "test@test.com";
        String name = "name";
        String password = "password";

        User user = User.builder()
                .email(email)
                .name(name)
                .build();

        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .name(name)
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .password(password)
                .build();


        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        doNothing().when(kafkaProducer).sendUserEvent(any(KafkaEvent.class));

        // when
        User createdUser = userService.createUser(user, password);

        // then
        assertNotNull(createdUser);
        assertEquals(email, createdUser.getEmail());
        assertEquals(name, createdUser.getName());

        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(any(UserEntity.class));

        ArgumentCaptor<KafkaEvent> kafkaEventCaptor = ArgumentCaptor.forClass(KafkaEvent.class);
        verify(kafkaProducer, times(1)).sendUserEvent(kafkaEventCaptor.capture());

        KafkaEvent capturedEvent = kafkaEventCaptor.getValue();
        assertEquals(KafkaActions.CREATED, capturedEvent.getAction());
        assertEquals(email, capturedEvent.getAfterUser().getEmail());
    }


    @Test
    void createUser_EmailAlreadyExist_ThrowException() {
        // given
        String email = "test@test.com";
        String name = "name";
        String password = "password";

        User user = User.builder()
                .email(email)
                .name(name)
                .build();

        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .name(name)
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .password(password)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.createUser(user, password)
        );

        assertEquals(email + " already exists", exception.getMessage());

        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(kafkaProducer, never()).sendUserEvent(any(KafkaEvent.class));
    }

    @Test
    void getAllUsers_Success() {
        // Given
        Long cursor = 1L;
        int pageSize = 10;

        List<UserEntity> userEntities = List.of(
                UserEntity.builder().email("test1@test.com").name("User1").build(),
                UserEntity.builder().email("test2@test.com").name("User2").build()
        );

        Mockito.when(userRepository.existsByIdx(cursor)).thenReturn(true);
        Mockito.when(userRepository.findAllByOrderByIdxDesc(cursor, PageRequest.of(0, pageSize))).thenReturn(userEntities);

        // When
        List<User> users = userService.getAllUsers(cursor, pageSize);

        // Then
        assertEquals(2, users.size());
        verify(kafkaProducer, times(1)).sendUserEvent(Mockito.any(KafkaEvent.class));
    }

    @Test
    void getAllUsers_CursorNotFound_ThrowsException() {
        // Given
        Long cursor = null;

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.getAllUsers(cursor, 10));
        verify(kafkaProducer, never()).sendUserEvent(Mockito.any(KafkaEvent.class));
    }

    @Test
    void getUserByIdx_Success() {
        // Given
        Long idx = 1L;
        UserEntity userEntity = UserEntity.builder().email("test@test.com").name("Test User").build();

        when(userRepository.findById(idx)).thenReturn(Optional.of(userEntity));

        // When
        User user = userService.getUserByIdx(idx);

        // Then
        assertNotNull(user);
        assertEquals("test@test.com", user.getEmail());
        verify(kafkaProducer, times(1)).sendUserEvent(Mockito.any(KafkaEvent.class));
    }

    @Test
    void getUserByIdx_NotFound_ThrowsException() {
        // Given
        Long idx = 1L;

        when(userRepository.findById(idx)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.getUserByIdx(idx));
        verify(kafkaProducer, never()).sendUserEvent(Mockito.any(KafkaEvent.class));
    }

    @Test
    void updateUser_Success() {
        // Given
        Long idx = 1L;
        User user = User.builder().email("updated@test.com").build();

        UserEntity existingEntity = UserEntity.builder().email("test@test.com").name("Test User").build();
        UserEntity updatedEntity = UserEntity.builder().email("updated@test.com").name("Test User").build();

        when(userRepository.findByIdx(idx)).thenReturn(Optional.of(existingEntity));
        when(userRepository.save(Mockito.any(UserEntity.class))).thenReturn(updatedEntity);

        // When
        User updatedUser = userService.updateUser(idx, user);

        // Then
        assertEquals("updated@test.com", updatedUser.getEmail());
        verify(kafkaProducer, times(1)).sendUserEvent(Mockito.any(KafkaEvent.class));
    }

    @Test
    void updateUser_UserNotFound_ThrowsException() {
        // Given
        Long idx = 1L;
        User user = User.builder().email("updated@test.com").build();

        when(userRepository.findByIdx(idx)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(idx, user));
        verify(kafkaProducer, never()).sendUserEvent(Mockito.any(KafkaEvent.class));
    }

    @Test
    void updatePassword_Success() {
        // Given
        Long idx = 1L;
        String oldPassword = "oldpass";
        String newPassword = "newpass";

        UserEntity existingEntity = UserEntity.builder().password(oldPassword).build();

        when(userRepository.findByIdx(idx)).thenReturn(Optional.of(existingEntity));
        when(userRepository.save(Mockito.any(UserEntity.class))).thenReturn(existingEntity);

        // When
        User updatedUser = userService.updatePassword(idx, oldPassword, newPassword);

        // Then
        assertNotNull(updatedUser);
        verify(kafkaProducer, times(1)).sendUserEvent(Mockito.any(KafkaEvent.class));
    }

    @Test
    void updatePassword_IncorrectOldPassword_ThrowsException() {
        // Given
        Long idx = 1L;
        String oldPassword = "wrongpass";
        String newPassword = "newpass";

        UserEntity existingEntity = UserEntity.builder().password("oldpass").build();

        when(userRepository.findByIdx(idx)).thenReturn(Optional.of(existingEntity));

        // When & Then
        assertThrows(IncorrectPasswordException.class, () -> userService.updatePassword(idx, oldPassword, newPassword));
        verify(kafkaProducer, never()).sendUserEvent(Mockito.any(KafkaEvent.class));
    }

    @Test
    void deleteUser_Success() {
        // Given
        Long idx = 1L;
        UserEntity existingEntity = UserEntity.builder().build();

        when(userRepository.findByIdx(idx)).thenReturn(Optional.of(existingEntity));

        // When
        boolean result = userService.deleteUser(idx);

        // Then
        assertTrue(result);
        verify(kafkaProducer, times(1)).sendUserEvent(Mockito.any(KafkaEvent.class));
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        // Given
        Long idx = 1L;

        when(userRepository.findByIdx(idx)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(idx));
        verify(kafkaProducer, never()).sendUserEvent(Mockito.any(KafkaEvent.class));
    }
}