package com.popytka.popytka.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.popytka.popytka.config.security.CustomUserDetails;
import com.popytka.popytka.entity.Country;
import com.popytka.popytka.entity.User;
import com.popytka.popytka.repository.CountryRepository;
import com.popytka.popytka.repository.UserRepository;
import com.popytka.popytka.service.EmailService;
import com.popytka.popytka.service.RoleService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final RoleService roleService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CountryRepository countryRepository;

    private static int CODE;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/home")
    public String home(Model model) {
        List<Country> countries = countryRepository.findAll();
        model.addAttribute("countries", countries);
        return "home";
    }

    @GetMapping("/login")
    public String showAuthorizationPage() {
        return "user/login";
    }

    @GetMapping("/registration")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "user/registration";
    }

    @Transactional
    @PostMapping("/registration")
    public String registrateUser(@ModelAttribute User user, Model model) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute(
                    "errorMessage",
                    "Пользователь с данной почтой уже зарегистрирован!"
            );
            return "user/registration";
        }
        user.setPassword(hashedPassword);
        user.setRole(roleService.getDefaultRoles());
        userRepository.save(user);
        return "user/login";
    }

    @GetMapping("/account")
    public String account(Model model, Authentication authentication) {
        if (authentication == null) {
            model.addAttribute("userId", 0);
            return "redirect:/login";
        } else {
            Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
            User user = userRepository.findById(userId).get();
            model.addAttribute("user", user);
            return "user/account";
        }
    }

    @GetMapping("/account/{id}/edit")
    public String userEdit(@PathVariable(value = "id") Long id, Model model) {
        User user = userRepository.findById(id).orElse(null);
        model.addAttribute("user", user);
        return "user/user-edit";
    }

    @Transactional
    @PostMapping("/account/{id}/edit")
    public String updateUser(
            @PathVariable(value = "id") Long id,
            @ModelAttribute User user,
            Model model
    ) {
        User originUser = userRepository.findById(id).get();
        originUser.setFirstName(user.getFirstName());
        originUser.setLastName(user.getLastName());
        originUser.setEmail(user.getEmail());
        originUser.setPhone(user.getPhone());
        User updatedUser = userRepository.save(originUser);
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
        Optional<User> foundUser = userRepository.findByEmail(email);
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
        if (password.equals(copyPassword)) {
            String hashPassword = passwordEncoder.encode(password);
            User user = userRepository.findByEmail(email).get();
            user.setPassword(hashPassword);
            userRepository.save(user);
            return "user/login";
        } else {
            model.addAttribute("errorMessage", "Пароли не совпадают!");
            model.addAttribute("userEmail", email);
        }
        return "user/edit-password";
    }

    @PostMapping("/import")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        String message;
        if (!file.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<User> data = objectMapper.readValue(file.getBytes(), new TypeReference<>() {
                });
                userRepository.saveAll(data);
                message = "Данные успешно сохранены в базу данных.";
                model.addAttribute("messageSuccess", message);
            } catch (Exception e) {
                e.printStackTrace();
                message = "Ошибка при импорте данных";
                model.addAttribute("messageError", message);
            }
        }
        List<Country> countries = countryRepository.findAll();
        model.addAttribute("country", countries);
        return "redirect:/home";
    }

    @GetMapping("/export")
    public void exportData(HttpServletResponse response) {
        try {
            List<User> users = userRepository.findAll();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            response.setContentType("application/json");
            response.setHeader("Content-Disposition", "attachment; filename=\"users.json\"");

            objectMapper.writeValue(response.getOutputStream(), users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
