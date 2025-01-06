package com.trip.user.service;

import com.trip.user.dto.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    // Created
    User createUser(User user, String Password);

    // Read
    User getUserByIdx(Long idx);
    List<User> getAllUsers(Long cursor, int pageSize);
    List<User> getUsersByEmailStartWith(Long cursor, String email, int pageSize);
    List<User> getUsersByNameStartWith(Long cursor, String name, int pageSize);

    // Update
    User updateUser(Long idx, User user);
    User updatePassword(Long idx, String oldPassword, String newPassword);

    // Delete
    boolean deleteUser(Long idx);
}
