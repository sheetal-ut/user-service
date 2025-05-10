package com.ps.user.service;

import com.ps.user.entity.User;

import java.util.List;

public interface UserService {
    User addUser(User user);
    List<User> getAllUsers();
    User getUserById(Long id);
    User updateUser(Long id, User user);
    String deleteUser(Long id);
}
