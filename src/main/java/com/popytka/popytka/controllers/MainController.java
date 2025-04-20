package com.popytka.popytka.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.popytka.popytka.EmailService;
import com.popytka.popytka.models.Booking;
import com.popytka.popytka.models.Country;
import com.popytka.popytka.models.User;
import com.popytka.popytka.repos.BookingRepository;
import com.popytka.popytka.repos.CountryRepository;
import com.popytka.popytka.repos.OrderRepository;
import com.popytka.popytka.repos.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Random;

@Controller
public class MainController {
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private static final Logger log = LoggerFactory.getLogger(MainController.class);
    private static final String SESSION_ID_KEY = "sessionId";
    public static Long UserID;
    public static boolean isAdmin;

    private static int CODE;

    @GetMapping("/")
    public String home(Model model) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        List<User> data = (List<User>) userRepository.getAllUser(); // Полученные данные из таблицы
//        String json = objectMapper.writeValueAsString(data);
//        String filePath = "src/main/resources/kurs.json";
//        try (FileWriter fileWriter = new FileWriter(filePath)) {
//            fileWriter.write(json);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Iterable<Country> countries = countryRepository.getCountries();
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        model.addAttribute("country", countries);
        model.addAttribute("title", "Главная страница");
        return "home";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        return "registration";
    }

    @Transactional
    @PostMapping("/registration")
    public String addUser(@RequestParam String first_name, @RequestParam String last_name, @RequestParam String phone,
                          @RequestParam String email, @RequestParam String password, Model model) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error_message", "Пользователь с данной почтой уже заргеистрирован");
            return "registration";
        }
        userRepository.addUser(first_name, last_name, phone, email, hashedPassword);
        return "redirect:/";
    }

    @GetMapping("/authorization")
    public String authorization(Model model) {
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        return "authorization";
    }

    @Transactional
    @PostMapping("/authorization")
    public String login(@RequestParam String email, @RequestParam String password, HttpServletRequest request, Model model) {
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        if (userRepository.existsByEmail(email)) {
            if (BCrypt.checkpw(password, userRepository.getPasswordUserByEmail(email))) {
                String userId = String.valueOf(userRepository.getUsersIdByEmail(email));
                HttpSession session = request.getSession();
                session.setAttribute(SESSION_ID_KEY, userId);
                isAdmin = userRepository.getRole(userRepository.getUsersIdByEmail(email));
                MDC.put(SESSION_ID_KEY, userId);
                UserID = Long.valueOf(userId);
                log.info("Пользователь {} вошел в систему ", userId);
                return "redirect:/account";
            } else {
                model.addAttribute("error_message", "Неверный пароль!");
                return "authorization";
            }
        } else {
            model.addAttribute("error_message", "Пользователя с данной почтой нет!");
            return "authorization";
        }
    }

    @GetMapping("/account")
    public String account(HttpServletRequest request, Model model) {
        if (UserID == null) {
            model.addAttribute("userId", 0);
            return "redirect:/authorization";
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
            User user = userRepository.getById(UserID);
            model.addAttribute("user", user);
            return "account";
        }
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, Model model){
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
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

    @GetMapping("/account/{idUser}/edit")
    public String userEdit(@PathVariable(value = "idUser") long id, Model model) {
        User user = userRepository.getById(id);
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }

        model.addAttribute("user", user);
        return "user-edit";
    }

    @Transactional
    @PostMapping("/account/{idUser}/edit")
    public String updateUser(@PathVariable(value = "idUser") long id, @RequestParam String first_name,
                             @RequestParam String last_name, @RequestParam String phone, @RequestParam String email, Model model) {

        User user = userRepository.getById(id);
        userRepository.updateUser(id, first_name, last_name, phone, email);
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        model.addAttribute("user", user);
        return "redirect:/account";
    }

    @GetMapping("/account/bookings/{idUser}")
    public String bookingsUser(@PathVariable(value = "idUser") Long id, Model model) {
        User user = userRepository.getById(id);
        List<Booking> booking = bookingRepository.findByUser_UserId(id);
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        model.addAttribute("user", user);
        model.addAttribute("booking", booking);
        return "bookings";
    }

    @GetMapping("/verification")
    public String verification(Model model) {
        return "verification";
    }

    @PostMapping("/verification")
    public String sendCode(@RequestParam("email") String email, Model model) {
        Random generateCode = new Random();
        CODE = generateCode.nextInt(1000, 9999);
        UserID = userRepository.getUsersIdByEmail(email);
        if(UserID == null){
            model.addAttribute("error_message", "e-mail не существует");
            return "verification";
        }
        String subject = "Verification";
        String message = "Your verification code: " + CODE;
        emailService.sendEmail(email, subject, message);
        return "check-email";
    }

    @GetMapping("/check-email")
    public String checkEmail(Model model) {
        return "check-email";
    }

    @PostMapping("/check-email")
    public String checkCode(@RequestParam("code") String code, Model model) {
        int userCode = Integer.parseInt(code);
        if (userCode == CODE) {
            return "edit-password";
        }
        else
            model.addAttribute("error_message", "Invalid code");
        return "redirect:/check-email";
    }

    @GetMapping("/edit-password")
    public String editPassword(Model model) {
        return "edit-password";
    }

    @PostMapping("/edit-password")
    public String setCode(@RequestParam("password") String password, @RequestParam("password2") String password2, Model model) {

        if (password.equals(password2)) {
            String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            userRepository.setPasswordById(UserID, hashPassword);
            UserID = null;
            return "authorization";
        }
        else
            model.addAttribute("error_message", "Passwords don't match");
        return "edit-password";
    }

    @PostMapping("/import")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        String message_success = null;
        String message_error = null;
        if (!file.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<User> data = objectMapper.readValue(file.getBytes(), new TypeReference<>() {
                });
                userRepository.saveAll(data);
                System.out.println("Данные успешно сохранены в базу данных.");
                message_success = "Данные успешно сохранены в базу данных.";
            } catch (Exception e) {
                e.printStackTrace();
                message_error = "Ошибка при импорте данных";

            }
        }
        model.addAttribute("message_success", message_success);
        model.addAttribute("message_error", message_error);
        Iterable<Country> countries = countryRepository.getCountries();
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        model.addAttribute("country", countries);
        model.addAttribute("title", "Главная страница");
        return "home";
    }

    @GetMapping("/export")
    public void exportData(HttpServletResponse response) {
        try {
            List<User> users = userRepository.getAllUser();

            // Создаем ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // Настраиваем ObjectMapper для сериализации даты и времени в формат ISO8601
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            // Устанавливаем заголовки для ответа
            response.setContentType("application/json");
            response.setHeader("Content-Disposition", "attachment; filename=\"users.json\"");

            // Преобразуем данные в JSON и записываем в выходной поток
            objectMapper.writeValue(response.getOutputStream(), users);
        } catch (IOException e) {
            e.printStackTrace();
            // Обработка ошибки
        }
    }

//    @GetMapping("/tour")
//    public String tourAdmin(HttpServletRequest request, Model model) {
//        // Получаем идентификатор пользователя из сессии
//        HttpSession session = request.getSession();
//        String userIdString = (String) session.getAttribute(SESSION_ID_KEY);
//
//        // Преобразуем строку в тип Long
//        Long userId = Long.valueOf(userIdString);
//        // Загружаем данные пользователя
//        boolean admin = userRepository.getRole(userId);
//
//        // Добавляем данные пользователя в модель
//        model.addAttribute("isAdmin", admin);
//
//        return "tour";
//    }

}