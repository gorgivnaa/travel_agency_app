package com.popytka.popytka.service.impl;

import com.popytka.popytka.entity.Role;
import com.popytka.popytka.entity.User;
import com.popytka.popytka.repository.UserRepository;
import com.popytka.popytka.service.RoleService;
import com.popytka.popytka.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final RoleService roleService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void updateUserRole(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + " not found!"));
        Role updatedRole = roleService.getById(roleId);
        if (!updatedRole.equals(user.getRole())) {
            user.setRole(updatedRole);
            userRepository.save(user);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User updateUser(Long id, User user) {
        User originUser = findById(id).get();
        originUser.setFirstName(user.getFirstName());
        originUser.setLastName(user.getLastName());
        originUser.setEmail(user.getEmail());
        originUser.setPhone(user.getPhone());
        return userRepository.save(originUser);
    }

    @Override
    public Optional<User> updatePassword(String email, String password, String copyPassword) {
        if (!password.equals(copyPassword)) {
            return Optional.empty();
        }
        String hashPassword = passwordEncoder.encode(password);
        User user = userRepository.findByEmail(email).get();
        user.setPassword(hashPassword);
        User updatedUser = userRepository.save(user);
        return Optional.of(updatedUser);
    }

    @Override
    public List<User> getByRoleName(String role) {
        return userRepository.findByRoleName(role);
    }

    @Override
    public Optional<User> registryUser(User user) {
        Optional<User> existedUser = findByEmail(user.getEmail());
        return existedUser.isPresent()
                ? Optional.empty()
                : Optional.of(createUser(user));
    }

    private User createUser(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        user.setRole(roleService.getDefaultRoles());
        return userRepository.save(user);
    }
}
