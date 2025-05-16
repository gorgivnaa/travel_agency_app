package com.popytka.popytka.controller;

import com.popytka.popytka.config.security.CustomUserDetails;
import com.popytka.popytka.controller.filter.TourFilter;
import com.popytka.popytka.entity.AdditionalService;
import com.popytka.popytka.entity.Country;
import com.popytka.popytka.entity.Hotel;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.entity.User;
import com.popytka.popytka.repository.AdditionalServiceRepository;
import com.popytka.popytka.repository.CountryRepository;
import com.popytka.popytka.repository.HotelRepository;
import com.popytka.popytka.repository.TourRepository;
import com.popytka.popytka.repository.UserRepository;
import com.popytka.popytka.service.TourService;
import com.popytka.popytka.util.CustomPage;
import com.popytka.popytka.util.FilterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/tours")
@Transactional(readOnly = true)
public class TourController {

    private final FilterUtil filterUtil;
    private final TourService tourService;
    private final UserRepository userRepository;
    private final TourRepository tourRepository;
    private final HotelRepository hotelRepository;
    private final CountryRepository countryRepository;
    private final AdditionalServiceRepository additionalServiceRepository;

    @GetMapping
    public String getAllTours(Authentication authentication, Model model) {
        List<Tour> tours;
        if (authentication == null) {
            tours = tourService.getAllTours(null);
        } else {
            Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
            tours = tourService.getAllTours(userId);
        }
        List<Country> countries = countryRepository.findAll();

        model.addAttribute("tours", tours);
        model.addAttribute("countries", countries);
        return "tour/tour";
    }

    @GetMapping("/search")
    public String getSearchedTours(Model model, @RequestParam("titleOrDescription") String titleOrDescription) {
        List<Tour> tours = tourService.getSearchedTours(titleOrDescription);
        List<Country> countries = countryRepository.findAll();

        model.addAttribute("tours", tours);
        model.addAttribute("countries", countries);
        return "tour/tour";
    }

    @GetMapping("/filter")
    public String getFilteredTours(
            Model model,
            @ModelAttribute TourFilter filter,
            Pageable pageable
    ) {
        filterUtil.addPriceFilter(filter);
        Page<Tour> pageTours = tourService.getFilteredTours(filter, pageable);
        CustomPage<Tour> tourCustomPage = new CustomPage<>(pageTours);
        List<Country> countries = countryRepository.findAll();

        model.addAttribute("tours", tourCustomPage.content());
        model.addAttribute("countries", countries);
        return "tour/tour";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable("id") Long id, Authentication authentication, Model model) {
        User user = null;
        if (authentication != null) {
            Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
            user = userRepository.findById(userId).get();
        }
        Tour tour = tourService.getById(id).get();
        List<AdditionalService> additionalServices = additionalServiceRepository.findAll();

        model.addAttribute("additionalServices", additionalServices);
        model.addAttribute("tour", tour);
        model.addAttribute("user", user);
        return "tour/tour-info";
    }

    @GetMapping("edit/{id}")
    public String getTourForEdit(@PathVariable("id") Long id, Model model) {
        Tour tour = tourRepository.findById(id).get();
        List<Country> countries = countryRepository.findAll();
        List<Hotel> hotels = hotelRepository.findAll();

        model.addAttribute("tour", tour);
        model.addAttribute("countries", countries);
        model.addAttribute("hotels", hotels);
        return "tour/tour-edit";
    }

    @GetMapping("/add")
    public String getTourForCreate(Model model) {
        List<Country> countries = countryRepository.findAll();
        List<Hotel> hotels = hotelRepository.findAll();

        model.addAttribute("hotels", hotels);
        model.addAttribute("countries", countries);
        return "tour/tour-add";
    }

    @PostMapping
    @Transactional
    public String createTour(
            @ModelAttribute Tour tour,
            @RequestParam("countryName") String countryName,
            @RequestParam("hotelName") String hotelName,
            Model model) {
        Country country = countryRepository.findByName(countryName).get();
        Hotel hotel = hotelRepository.findByName(hotelName).get();
        Tour createdTour = tourService.createTour(tour, country, hotel);

        log.info("Tour created : {}", createdTour.toString());
        return "redirect:/tours";
    }

    @Transactional
    @PutMapping("/{id}")
    public String tourUpdate(
            @PathVariable("id") Long id,
            @ModelAttribute Tour tour
    ) {
        Hotel hotel = hotelRepository.findByName(tour.getHotel().getName()).get();
        Country country = countryRepository.findByName(tour.getCountry().getName()).get();
        tourService.updateTour(id, tour, country, hotel);
        return "redirect:/tours";
    }

    @Transactional
    @DeleteMapping("/{id}")
    public String deleteTour(@PathVariable("id") Long id) {
        tourRepository.deleteById(id);
        return "redirect:/tours";
    }
}
