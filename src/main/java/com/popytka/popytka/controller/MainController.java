package com.popytka.popytka.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.popytka.popytka.entity.Country;
import com.popytka.popytka.entity.User;
import com.popytka.popytka.repository.CountryRepository;
import com.popytka.popytka.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final UserRepository userRepository;
    private final CountryRepository countryRepository;

    @GetMapping("/home")
    public String home(Model model) {
        List<Country> countries = countryRepository.findAll();
        model.addAttribute("countries", countries);
        return "home";
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
