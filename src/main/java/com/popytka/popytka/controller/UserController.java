package com.popytka.popytka.controller;

import com.popytka.popytka.config.security.CustomUserDetails;
import com.popytka.popytka.entity.Role;
import com.popytka.popytka.entity.User;
import com.popytka.popytka.service.EmailService;
import com.popytka.popytka.service.RoleService;
import com.popytka.popytka.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final EmailService emailService;

    private static int CODE;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showUsers(Model model) {
        List<User> users = userService.getAllUsers();
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("users", users);
        model.addAttribute("roles", roles);
        return "user/users";
    }

    @Transactional
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String updateUserRole(@PathVariable("id") Long userId, @RequestParam Long roleId) {
        userService.updateUserRole(userId, roleId);
        return "redirect:/users";
    }

    @GetMapping("/registration")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "user/registration";
    }

    @Transactional
    @PostMapping("/registration")
    public String registrateUser(@ModelAttribute User user, Model model) {
        if (userService.registryUser(user).isPresent()) {
            return "user/login";
        }
        model.addAttribute(
                "errorMessage",
                "Пользователь с данной почтой уже зарегистрирован!"
        );
        return "user/registration";
    }

    @GetMapping("/login")
    public String showAuthorizationPage() {
        return "user/login";
    }

    @GetMapping("/account")
    public String account(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        User user = userService.findById(userId).get();
        model.addAttribute("user", user);
        return "user/account";
    }

    @GetMapping("/account/{id}/edit")
    public String showUserData(@PathVariable(value = "id") Long id, Model model) {
        User user = userService.findById(id).get();
        model.addAttribute("user", user);
        return "user/user-edit";
    }

    @Transactional
    @PutMapping("/account/{id}/edit")
    public String updateUser(
            @PathVariable(value = "id") Long id,
            @ModelAttribute User user,
            Model model
    ) {
        User updatedUser = userService.updateUser(id, user);
        model.addAttribute("user", updatedUser);
        return "redirect:/account";
    }

    @GetMapping("/check-email")
    public String checkEmail() {
        return "user/check-email";
    }

    @GetMapping("/verification")
    public String verification() {
        return "user/verification";
    }

    @PostMapping("/verification")
    public String sendCode(@RequestParam("email") String email, Model model) {
        Random generateCode = new Random();
        CODE = generateCode.nextInt(1000, 9999);
        Optional<User> foundUser = userService.findByEmail(email);
        if (foundUser.isEmpty()) {
            model.addAttribute("errorMessage", "e-mail не существует");
            return "user/verification";
        }
        String subject = "Verification";
        String message = "Your verification code: " + CODE;
        emailService.sendEmail(foundUser.get().getEmail(), subject, message);
        model.addAttribute("userEmail", email);
        return "user/check-email";
    }

    @PostMapping("/check-email")
    public String checkCode(@RequestParam("code") String code, @RequestParam("userEmail") String email, Model model) {
        int userCode = Integer.parseInt(code);
        if (userCode == CODE) {
            model.addAttribute("userEmail", email);
            return "user/edit-password";
        }
        model.addAttribute("errorMessage", "Invalid code");
        return "redirect:/check-email";
    }

    @GetMapping("/edit-password")
    public String editPassword() {
        return "user/edit-password";
    }

    @PostMapping("/edit-password")
    public String setCode(
            @RequestParam("password") String password,
            @RequestParam("copyPassword") String copyPassword,
            @RequestParam("userEmail") String email,
            Model model
    ) {
        if (userService.updatePassword(email, password, copyPassword).isEmpty()) {
            model.addAttribute("errorMessage", "Пароли не совпадают!");
            model.addAttribute("userEmail", email);
            return "user/edit-password";
        }
        return "user/login";
    }
}
