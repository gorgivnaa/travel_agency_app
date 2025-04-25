package com.popytka.popytka.controller;

import com.popytka.popytka.controller.filter.TourFilter;
import com.popytka.popytka.dto.TourDTO;
import com.popytka.popytka.entity.AdditionalService;
import com.popytka.popytka.entity.Country;
import com.popytka.popytka.entity.Hotel;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.entity.User;
import com.popytka.popytka.repository.CountryRepository;
import com.popytka.popytka.repository.HotelRepository;
import com.popytka.popytka.repository.ServiceRepository;
import com.popytka.popytka.repository.TourRepository;
import com.popytka.popytka.repository.UserRepository;
import com.popytka.popytka.service.TourService;
import com.popytka.popytka.util.CustomPage;
import com.popytka.popytka.util.FilterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import java.util.List;

import static com.popytka.popytka.controller.MainController.UserID;
import static com.popytka.popytka.controller.MainController.isAdmin;

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
    private final ServiceRepository serviceRepository;

    @GetMapping
    public String getAllTours(
            Model model,
            @ModelAttribute TourFilter filter,
            Pageable pageable
    ) {
        filterUtil.addPriceFilter(filter);
        Page<Tour> pageTours = tourService.getAllServices(filter, pageable);
        List<Country> countries = countryRepository.findAll();
        CustomPage<Tour> tourCustomPage = new CustomPage<>(pageTours);

        model.addAttribute("userId", UserID == null ? 0 : 1);
        model.addAttribute("tours", tourCustomPage);
        model.addAttribute("countries", countries);
        model.addAttribute("isAdmin", isAdmin);
        return "tour/tour";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findById(UserID).orElse(null);
        Tour tour = tourService.getById(id).get();
        Hotel hotel = tour.getHotel();
        List<AdditionalService> additionalServices = serviceRepository.findAll();

        model.addAttribute("userId", UserID == null ? 0 : 1);
        model.addAttribute("isAdmin", isAdmin);
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

        model.addAttribute("userId", UserID == null ? 0 : 1);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("tour", tour);
        model.addAttribute("countries", countries);
        model.addAttribute("hotels", hotels);
        return "tour/tour-edit";
    }

    @GetMapping("/add")
    public String getTourForCreate(Model model) {
        List<Country> countries = countryRepository.findAll();
        List<Hotel> hotels = hotelRepository.findAll();

        model.addAttribute("userId", UserID == null ? 0 : 1);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("hotels", hotels);
        model.addAttribute("countries", countries);
        return "tour/tour-add";
    }

    @PostMapping
    @Transactional
    public String createTour(@ModelAttribute TourDTO tourDTO, Model model) {
        Country country = countryRepository.findByName(tourDTO.getCountryName()).get();
        Hotel hotel = hotelRepository.findByName(tourDTO.getHotelName()).get();
        tourService.createTour(tourDTO, country, hotel);

        model.addAttribute("userId", UserID == null ? 0 : 1);
        model.addAttribute("isAdmin", isAdmin);
        return "redirect:/tours";
    }

    @Transactional
    @PutMapping("/{id}")
    public String tourUpdate(
            @PathVariable("id") Long id,
            @ModelAttribute TourDTO tourDTO,
            Model model
    ) {
        Hotel hotel = hotelRepository.findByName(tourDTO.getHotelName()).get();
        Country country = countryRepository.findByName(tourDTO.getCountryName()).get();
        tourService.updateTour(id, tourDTO, country, hotel);
        return "redirect:/tours";
    }

    @Transactional
    @DeleteMapping("/{id}")
    public String deleteTour(@PathVariable("id") Long id) {
        tourRepository.deleteById(id);
        return "redirect:/tours";
    }
}
