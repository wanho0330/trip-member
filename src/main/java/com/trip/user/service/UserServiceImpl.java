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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final KafkaProducer kafkaProducer;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User createUser(User user, String password) {
        logger.info("UserService-createUser-Creating user {}", user);

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            logger.info("UserService-createUser-User already exists");
            throw new EmailAlreadyExistsException(user.getEmail());
        }

        User registeredUser = User.builder()
                .email(user.getEmail())
                .name(user.getName())
                .status(Status.ACTIVE)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        UserEntity registeredUserEntity = userRepository.save(registeredUser.toEntity(password));
        registeredUser = User.formEntity(registeredUserEntity);

        KafkaEvent kafkaEvent = KafkaEvent.builder()
                .action(KafkaActions.CREATED)
                .afterUser(registeredUser)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaProducer.sendUserEvent(kafkaEvent);

        logger.info("UserService-createUser-Created user {}", registeredUser);
        return registeredUser;
    }

    @Override
    public List<User> getAllUsers(Long cursor, int pageSize) {
        logger.info("UserService-getAllUsers-Getting all users");
        if(cursor == null) {
            logger.info("UserService-getAllUsers-Cursor is null");
            throw new UserNotFoundException();
        }

        if(!userRepository.existsByIdx(cursor)) {
            logger.info("UserService-getAllUsers-Cursor does not exist");
            throw new UserNotFoundException(cursor);
        }

        if(pageSize <= 0 || pageSize > 100) {
            pageSize = 15;
        }

        Pageable pageable = PageRequest.of(0, pageSize);
        List<User> users = User.fromEntityList(userRepository.findAllByOrderByIdxDesc(cursor, pageable));

        KafkaEvent kafkaEvent = KafkaEvent.builder()
                .action(KafkaActions.GET_ALL_USERS)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaProducer.sendUserEvent(kafkaEvent);

        logger.info("UserService-getAllUsers-Get all users");
        return users;
    }

    @Override
    public User getUserByIdx(Long idx) {
        logger.info("UserService-getUserByIdx-Getting user {}", idx);
        User user = userRepository.findById(idx)
                .map(User::formEntity)
                .orElseThrow(() -> {
                    logger.info("UserService-getUserByIdx-User does not exist");
                    return new UserNotFoundException(idx);
                });

        KafkaEvent kafkaEvent = KafkaEvent.builder()
                .action(KafkaActions.GET_USERS_BY_IDX)
                .searchKeyword(idx.toString())
                .timestamp(LocalDateTime.now())
                .build();

        kafkaProducer.sendUserEvent(kafkaEvent);

        logger.info("UserService-getUserByIdx-Get user {}", idx);
        return user;
    }

    @Override
    public List<User> getUsersByEmailStartWith(Long cursor, String email, int pageSize) {
        logger.info("UserService-getUsersByEmailStartWith-Getting all users by email {}", email);
        if(cursor == null) {
            logger.info("UserService-getUsersByEmailStartWith-Cursor is null");
            throw new UserNotFoundException();
        }

        if(!userRepository.existsByIdx(cursor)) {
            logger.info("UserService-getUsersByEmailStartWith-Cursor does not exist");
            throw new UserNotFoundException(cursor);
        }

        if(pageSize <= 0 || pageSize > 100) {
            pageSize = 15;
        }

        Pageable pageable = PageRequest.of(0, pageSize);
        List<User> users = User.fromEntityList(userRepository.findByEmailStartsWith(cursor, email, pageable));

        KafkaEvent kafkaEvent = KafkaEvent.builder()
                .action(KafkaActions.GET_USERS_BY_EMAIL)
                .searchKeyword(email)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaProducer.sendUserEvent(kafkaEvent);

        logger.info("UserService-getUsersByEmailStartWith-Get all users by email {}", email);
        return users;
    }

    @Override
    public List<User> getUsersByNameStartWith(Long cursor, String name, int pageSize) {
        logger.info("UserService-getUsersByNameStartWith-Getting all users by name {}", name);
        if(cursor == null) {
            logger.info("UserService-getUsersByNameStartWith-Cursor is null");
            throw new UserNotFoundException();
        }

        if(!userRepository.existsByIdx(cursor)) {
            logger.info("UserService-getUsersByNameStartWith-Cursor does not exist");
            throw new UserNotFoundException(cursor);
        }

        if(pageSize <= 0 || pageSize > 100) {
            pageSize = 15;
        }

        Pageable pageable = PageRequest.of(0, pageSize);
        List<User> users = User.fromEntityList(userRepository.findByNameStartsWith(cursor, name, pageable));

        KafkaEvent kafkaEvent = KafkaEvent.builder()
                .action(KafkaActions.GET_USERS_BY_NAME)
                .searchKeyword(name)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaProducer.sendUserEvent(kafkaEvent);

        logger.info("UserService-getUsersByNameStartWith-Get all users by name {}", name);
        return users;
    }

    @Override
    public User updateUser(Long idx, User user) {
        logger.info("UserService-updateUser-Updating user {}", idx);
        UserEntity userEntity = userRepository.findByIdx(idx)
                .orElseThrow(() -> {
                    logger.info("UserService-updateUser-User does not exist");
                    return new UserNotFoundException(idx);
                });

        User beforeUser = User.formEntity(userEntity);
        User updatedUser = User.builder()
                .idx(idx)
                .email(user.getEmail())
                .name(userEntity.getName())
                .status(userEntity.getStatus())
                .createAt(userEntity.getCreatedAt())
                .updateAt(userEntity.getUpdatedAt())
                .lastLoginIp(userEntity.getLastLoginIp())
                .lastLoginAt(userEntity.getLastLoginAt())
                .failedAttempts(userEntity.getFailedAttempts())
                .build();


        userRepository.save(updatedUser.toEntity());

        KafkaEvent kafkaEvent = KafkaEvent.builder()
                .action(KafkaActions.UPDATED)
                .beforeUser(beforeUser)
                .afterUser(updatedUser)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaProducer.sendUserEvent(kafkaEvent);

        logger.info("UserService-updateUser-Updated user {}", idx);
        return updatedUser;
    }

    @Override
    public User updatePassword(Long idx, String oldPassword, String newPassword) {
        logger.info("UserService-updatePassword-Updating password {}", idx);
        UserEntity userEntity = userRepository.findByIdx(idx)
                .orElseThrow(() -> {
                    logger.info("UserService-updatePassword-User does not exist");
                    return new UserNotFoundException(idx);
                });

        if (!userEntity.getPassword().equals(oldPassword)) {
            logger.info("UserService-updatePassword-Old password does not match");
            throw new IncorrectPasswordException();
        }

        User updatedUser = User.builder()
                .idx(idx)
                .build();

        userRepository.save(updatedUser.toEntity(newPassword));

        KafkaEvent kafkaEvent = KafkaEvent.builder()
                .action(KafkaActions.PASSWORD_UPDATED)
                .beforeUser(User.formEntity(userEntity))
                .afterUser(updatedUser)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaProducer.sendUserEvent(kafkaEvent);

        logger.info("UserService-updatePassword-Updated password {}", idx);
        return updatedUser;
    }

    @Override
    public boolean deleteUser(Long idx) {
        logger.info("UserService-deleteUser-Deleting user {}", idx);
        userRepository.findByIdx(idx)
                .orElseThrow(() -> {
                    logger.info("UserService-deleteUser-User does not exist");
                    return new UserNotFoundException(idx);
                });

        User deletedUser = User.builder()
                .idx(idx)
                .status(Status.DELETED)
                .build();


        userRepository.save(deletedUser.toEntity());

        KafkaEvent kafkaEvent = KafkaEvent.builder()
                .action(KafkaActions.DELETED)
                .beforeUser(deletedUser)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaProducer.sendUserEvent(kafkaEvent);

        logger.info("UserService-deleteUser-Deleted user {}", idx);
        return true;
    }

}
