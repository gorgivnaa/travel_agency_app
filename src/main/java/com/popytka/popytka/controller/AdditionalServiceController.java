package com.popytka.popytka.controller;

import com.popytka.popytka.controller.filter.AdditionalServiceFilter;
import com.popytka.popytka.entity.AdditionalService;
import com.popytka.popytka.service.ASService;
import com.popytka.popytka.util.CustomPage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/additionalServices")
public class AdditionalServiceController {

    private final ASService additionalServiceService;

    @GetMapping
    public String getServices(
            Model model,
            @ModelAttribute AdditionalServiceFilter additionalServiceFilter,
            Pageable pageable
    ) {
        Page<AdditionalService> additionalServicesPage = additionalServiceService.getAdditionalServices(
                additionalServiceFilter, pageable
        );
        CustomPage<AdditionalService> additionalServiceCustomPage = new CustomPage<>(additionalServicesPage);
        model.addAttribute("newService", new AdditionalService());
        model.addAttribute("additionalServices", additionalServiceCustomPage);
        return "additional-service/services";
    }

    @GetMapping("/search")
    public String searchAdditionalServices(
            Model model,
            @RequestParam("nameOrDescription") String nameOrDescription,
            Pageable pageable
    ) {
        Page<AdditionalService> additionalServicesPage = additionalServiceService.searchAdditionalServices(
                nameOrDescription, pageable
        );
        CustomPage<AdditionalService> additionalServiceCustomPage = new CustomPage<>(additionalServicesPage);
        model.addAttribute("newService", new AdditionalService());
        model.addAttribute("additionalServices", additionalServiceCustomPage);
        return "additional-service/services";
    }

    @PostMapping
    @Transactional
    public String addNewService(@ModelAttribute AdditionalService additionalService) {
        additionalServiceService.createAS(additionalService);
        return "redirect:/additionalServices";
    }

    @Transactional
    @DeleteMapping("/{id}")
    public String deleteAdditionalService(@PathVariable("id") Long additionalServiceId) {
        additionalServiceService.deleteAS(additionalServiceId);
        return "redirect:/additionalServices";
    }

    @Transactional
    @PutMapping("/{id}")
    public String updateAdditionalService(
            @PathVariable("id") Long additionalServiceId,
            @ModelAttribute AdditionalService additionalService
    ) {
        additionalServiceService.updateAdditionalService(additionalServiceId, additionalService);
        return "redirect:/additionalServices";
    }
}
