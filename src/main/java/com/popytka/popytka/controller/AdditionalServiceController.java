package com.popytka.popytka.controller;

import com.popytka.popytka.entity.AdditionalService;
import com.popytka.popytka.repository.ServiceRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.popytka.popytka.controller.MainController.UserID;
import static com.popytka.popytka.controller.MainController.isAdmin;

@Controller
@RequiredArgsConstructor
@RequestMapping("/additionalServices")
public class AdditionalServiceController {

    private final ServiceRepository serviceRepository;
    private List<AdditionalService> filteredAdditionalServices = new ArrayList<>();

    @GetMapping
    public String getServices(Model model, @RequestParam(required = false) String sort) {
        model.addAttribute("userId", UserID==null?0:1);
        model.addAttribute("isAdmin", isAdmin);
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
        model.addAttribute("additionalServices", additionalServices);
        return "additional-service/services";
    }

    @GetMapping("/search")
    public String searchTour(@RequestParam("query") String query, Model model) {
        model.addAttribute("userId", UserID==null?0:1);
        model.addAttribute("isAdmin", isAdmin);
        filteredAdditionalServices = serviceRepository.findByNameContainingOrDescriptionContaining(query, query);
        if (filteredAdditionalServices.isEmpty()) {
            model.addAttribute("message", "Результатов по данному запросу не найдено.");
        } else {
            model.addAttribute("additionalServices", filteredAdditionalServices);
        }
        return "additional-service/services";
    }

    @PostMapping
    @Transactional
    public String addNewService(
            HttpServletRequest request,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price
    ) {
        AdditionalService createdAdditionalService = AdditionalService.builder()
                .name(name)
                .description(description)
                .price(new BigDecimal(price))
                .build();
        serviceRepository.save(createdAdditionalService);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }
}
