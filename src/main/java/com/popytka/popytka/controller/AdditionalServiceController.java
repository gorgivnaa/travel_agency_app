package com.popytka.popytka.controller;

import com.popytka.popytka.controller.filter.AdditionalServiceFilter;
import com.popytka.popytka.dto.AdditionalServiceDTO;
import com.popytka.popytka.entity.AdditionalService;
import com.popytka.popytka.service.ASService;
import com.popytka.popytka.util.CustomPage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.popytka.popytka.controller.MainController.UserID;
import static com.popytka.popytka.controller.MainController.isAdmin;

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

        model.addAttribute("userId", UserID == null ? 0 : 1);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("additionalServices", additionalServiceCustomPage);
        return "additional-service/services";
    }

    @PostMapping
    @Transactional
    public String addNewService(@ModelAttribute AdditionalServiceDTO additionalServiceDTO) {
        additionalServiceService.createAS(additionalServiceDTO);
        return "redirect:/additionalServices";
    }
}
