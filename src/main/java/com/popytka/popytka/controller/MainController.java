package com.popytka.popytka.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.popytka.popytka.entity.Country;
import com.popytka.popytka.entity.User;
import com.popytka.popytka.repository.CountryRepository;
import com.popytka.popytka.repository.UserRepository;
import com.popytka.popytka.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;

    private static final Logger log = LoggerFactory.getLogger(MainController.class);
    private static final String SESSION_ID_KEY = "sessionId";
    public static Long UserID = null;
    public static boolean isAdmin = false;
    private static int CODE;

    @GetMapping("/")
    public String home(Model model) {
        List<Country> countries = countryRepository.findAll();
        model.addAttribute("userId", UserID == null ? 0 : 1);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("countries", countries);
        return "home";
    }

    @GetMapping("/registration")
    public String registration() {
        return "user/registration";
    }

    @Transactional
    @PostMapping("/registration")
    public String addUser(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model
    ) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute(
                    "errorMessage", "Пользователь с данной почтой уже заргеистрирован"
            );
            return "user/registration";
        }

        User createdUser = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .email(email)
                .password(hashedPassword)
                .isAdmin(false)
                .build();
        userRepository.save(createdUser);
        return "redirect:/";
    }

    @GetMapping("/authorization")
    public String authorization(Model model) {
        model.addAttribute("userId", UserID == null ? 0 : 1);
        model.addAttribute("isAdmin", isAdmin);
        return "user/authorization";
    }

    @Transactional
    @PostMapping("/authorization")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletRequest request,
            Model model
    ) {
        model.addAttribute("userId", UserID == null ? 0 : 1);
        model.addAttribute("isAdmin", isAdmin);
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User loggedUser = userOptional.get();
            if (BCrypt.checkpw(password, loggedUser.getPassword())) {
                String userId = String.valueOf(loggedUser.getId());
                HttpSession session = request.getSession();
                session.setAttribute(SESSION_ID_KEY, userId);
                isAdmin = loggedUser.getIsAdmin();
                MDC.put(SESSION_ID_KEY, userId);
                UserID = Long.valueOf(userId);
                log.info("Пользователь {} вошел в систему ", userId);
                return "redirect:/account";
            } else {
                model.addAttribute("errorMessage", "Неверный пароль!");
                return "user/authorization";
            }
        } else {
            model.addAttribute("errorMessage", "Пользователя с данной почтой нет!");
            return "user/authorization";
        }
    }

    @GetMapping("/account")
    public String account(Model model) {
        if (UserID == null) {
            model.addAttribute("userId", 0);
            return "redirect:/authorization";
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
            User user = userRepository.findById(UserID).get();
            model.addAttribute("user", user);
            return "user/account";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, Model model) {
        model.addAttribute("userId", UserID == null ? 0 : 1);
        model.addAttribute("isAdmin", isAdmin);
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute(SESSION_ID_KEY);
        if (userId != null) {
            session.invalidate();
            isAdmin = false;
            UserID = null;
            log.info("Пользователь {} вышел из системы", userId);
        }
        return "redirect:/authorization";
    }

    @GetMapping("/account/{id}/edit")
    public String userEdit(@PathVariable(value = "id") Long id, Model model) {
        User user = userRepository.findById(id).orElse(null);
        model.addAttribute("userId", UserID == null ? 0 : 1);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("user", user);
        return "user/user-edit";
    }

    @Transactional
    @PostMapping("/account/{id}/edit")
    public String updateUser(
            @PathVariable(value = "id") Long id,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String phone,
            @RequestParam String email,
            Model model
    ) {
        User user = userRepository.findById(id).get();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        User updatedUser = userRepository.save(user);
        model.addAttribute("userId", UserID == null ? 0 : 1);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("user", updatedUser);
        return "redirect:/account";
    }

    @GetMapping("/verification")
    public String verification() {
        return "user/verification";
    }

    @PostMapping("/verification")
    public String sendCode(@RequestParam("email") String email, Model model) {
        Random generateCode = new Random();
        CODE = generateCode.nextInt(1000, 9999);
        UserID = userRepository.findByEmail(email).get().getId();
        if (UserID == null) {
            model.addAttribute("errorMessage", "e-mail не существует");
            return "user/verification";
        }
        String subject = "Verification";
        String message = "Your verification code: " + CODE;
        emailService.sendEmail(email, subject, message);
        return "user/check-email";
    }

    @GetMapping("/check-email")
    public String checkEmail() {
        return "user/check-email";
    }

    @PostMapping("/check-email")
    public String checkCode(@RequestParam("code") String code, Model model) {
        int userCode = Integer.parseInt(code);
        if (userCode == CODE) {
            return "user/edit-password";
        } else
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
            Model model
    ) {
        if (password.equals(copyPassword)) {
            String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            User user = userRepository.findById(UserID).get();
            user.setPassword(hashPassword);
            userRepository.save(user);
            UserID = null;
            return "user/authorization";
        } else {
            model.addAttribute("errorMessage", "Пароли не совпадают!");
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
        model.addAttribute("userId", UserID == null ? 0 : 1);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("country", countries);
        model.addAttribute("title", "Главная страница");
        return "home";
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
