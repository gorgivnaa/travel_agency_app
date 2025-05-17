package com.popytka.popytka.service;

import com.popytka.popytka.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAllUsers();

    void updateUserRole(Long userId, Long roleId);

    Optional<User> registryUser(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    User updateUser(Long id, User user);

    Optional<User> updatePassword(String email, String password, String copyPassword);
}
