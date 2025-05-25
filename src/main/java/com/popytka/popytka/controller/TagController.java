package com.popytka.popytka.controller;

import com.popytka.popytka.entity.Country;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.repository.CountryRepository;
import com.popytka.popytka.service.TourService;
import com.popytka.popytka.service.TourTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TagController {

    private final TourService tourService;
    private final TourTagService tourTagService;
    private final CountryRepository countryRepository;

    @PostMapping("/survey/submit")
    public String getToursByTags(@RequestParam String tags, Model model) {
        List<Tour> tours = tourTagService.getToursByTags(tags);
        List<Country> countries = countryRepository.findAll();

        model.addAttribute("tours", tours);
        model.addAttribute("countries", countries);
        return "tour/tour";
    }
}
