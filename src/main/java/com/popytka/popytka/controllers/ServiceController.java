package com.popytka.popytka.controllers;

import com.popytka.popytka.models.Service;
import com.popytka.popytka.repos.ServiceRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.popytka.popytka.controllers.MainController.UserID;
import static com.popytka.popytka.controllers.MainController.isAdmin;

@Controller
    public class ServiceController {
    List<Service> filteredServices = new ArrayList<>();
    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping("/services")
    public String servicesMain(Model model, @RequestParam(required = false) String sort, HttpSession session) {
        List<Service> services = serviceRepository.getServices();
        if (sort != null) {
            switch (sort) {
                case "asc":
                    services.sort(Comparator.comparing(Service::getService_name));
                    break;
                case "desc":
                    services.sort(Comparator.comparing(Service::getService_name).reversed());
                    break;
                case "descPrice":
                    services.sort(Comparator.comparing(Service::getPrice).reversed());
                    break;
                case "ascPrice":
                    services.sort(Comparator.comparing(Service::getPrice));
                    break;
            }
        }
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        model.addAttribute("service", services);
        model.addAttribute("isAdmin", isAdmin);
        return "services";
    }


    @GetMapping("/services/search")
    public String searchTour(@RequestParam("query") String query, Model model) {
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        filteredServices = serviceRepository.searchService(query);
        if (filteredServices.isEmpty()) {
            model.addAttribute("message", "Результатов по данному запросу не найдено.");
        } else {
            model.addAttribute("service", filteredServices);
        }
        model.addAttribute("isAdmin", isAdmin);
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("iserId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        return "services";
    }

    @Transactional
    @PostMapping("/services/add")
    public String addNewService(HttpServletRequest request, Model model, @RequestParam("service_name") String serviceName, @RequestParam("servicePrice") double price) {
        // Обработка запроса на добавление нового отеля
        // Например, установите атрибут модели, указывающий на необходимость отображения формы
        serviceRepository.addService(serviceName, price);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

}
