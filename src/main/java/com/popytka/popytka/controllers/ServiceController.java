package com.popytka.popytka.controllers;

import com.popytka.popytka.models.AdditionalService;
import com.popytka.popytka.repository.ServiceRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.popytka.popytka.controllers.MainController.UserID;
import static com.popytka.popytka.controllers.MainController.isAdmin;

@Controller
public class ServiceController {
    List<AdditionalService> filteredAdditionalServices = new ArrayList<>();
    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping("/services")
    public String servicesMain(Model model, @RequestParam(required = false) String sort, HttpSession session) {
        List<AdditionalService> additionalServices = serviceRepository.findAll();
        if (sort != null) {
            switch (sort) {
                case "asc":
                    additionalServices.sort(Comparator.comparing(AdditionalService::getName));
                    break;
                case "desc":
                    additionalServices.sort(Comparator.comparing(AdditionalService::getName).reversed());
                    break;
                case "descPrice":
                    additionalServices.sort(Comparator.comparing(AdditionalService::getPrice).reversed());
                    break;
                case "ascPrice":
                    additionalServices.sort(Comparator.comparing(AdditionalService::getPrice));
                    break;
            }
        }
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        model.addAttribute("service", additionalServices);
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
        filteredAdditionalServices = serviceRepository.findByNameContaining(query);
        if (filteredAdditionalServices.isEmpty()) {
            model.addAttribute("message", "Результатов по данному запросу не найдено.");
        } else {
            model.addAttribute("service", filteredAdditionalServices);
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
    public String addNewService(
            HttpServletRequest request,
            @RequestParam("service_name") String serviceName,
            @RequestParam("service_description") String serviceDescription,
            @RequestParam("servicePrice") double price
    ) {
        AdditionalService createdAdditionalService = AdditionalService.builder()
                .name(serviceName)
                .description(serviceDescription)
                .price(new BigDecimal(price))
                .build();
        serviceRepository.save(createdAdditionalService);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

}
