package com.popytka.popytka.controller;

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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.popytka.popytka.controller.MainController.UserID;
import static com.popytka.popytka.controller.MainController.isAdmin;

@Controller
@RequestMapping("/tours")
@RequiredArgsConstructor
public class TourController {

    private List<Tour> filteredTours = new ArrayList<>();

    private final UserRepository userRepository;
    private final TourRepository tourRepository;
    private final HotelRepository hotelRepository;
    private final CountryRepository countryRepository;
    private final ServiceRepository serviceRepository;

    @GetMapping
    public String getAllTours(Model model, @RequestParam(required = false) String sort) {
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
        }
        List<Tour> tours = tourRepository.findAll();

        if (sort != null) {
            if (sort.equals("asc")) {
                tours.sort(Comparator.comparing(Tour::getTitle));
            } else if (sort.equals("desc")) {
                tours.sort(Comparator.comparing(Tour::getTitle).reversed());
            } else if (sort.equals("descPrice")) {
                tours.sort(Comparator.comparing(Tour::getPrice).reversed());
            } else if (sort.equals("ascPrice")) {
                tours.sort(Comparator.comparing(Tour::getPrice));
            }
        }
        model.addAttribute("tours", tours);
        model.addAttribute("isAdmin", isAdmin);
        return "tour/tour";
    }

    @GetMapping("/filters")
    public String getFilteredTours(
            Model model,
            @RequestParam(required = false) String countryName,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
        }
        model.addAttribute("isAdmin", isAdmin);
        List<Tour> tours;
        if ("".equals(countryName)) {
            countryName = null;
        }

        if (countryName == null && maxPrice == null) {
            tours = tourRepository.findAll();
            filteredTours = tours;
        } else if (countryName != null && maxPrice == null) {
            tours = tourRepository.findByCountryName(countryName);
            filteredTours = tours;
        } else if (countryName == null && maxPrice != null) {
            tours = tourRepository.findByPriceLessThanEqual(maxPrice);
            filteredTours = tours;
        } else {
            tours = tourRepository.findByCountryNameAndPriceLessThanEqual(countryName, maxPrice);
            filteredTours = tours;
        }

        if (filteredTours.isEmpty()) {
            model.addAttribute("noResults", true);
        }
        model.addAttribute("tour", filteredTours);
        return "tour/tour-filters";
    }

    @GetMapping("/filters/{sort}")
    public String getFilteredAndSortedTours(@PathVariable(value = "sort") String sort, Model model) {
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
        }
        model.addAttribute("isAdmin", isAdmin);
        List<Tour> tours = filteredTours;
        switch (sort) {
            case "asc": {
                tours.sort(Comparator.comparing(Tour::getTitle));
            }
            break;
            case "desc": {
                tours.sort(Comparator.comparing(Tour::getTitle).reversed());
                break;
            }
            case "ascPrice": {
                tours.sort(Comparator.comparing(Tour::getPrice).reversed());
                break;
            }
            case "descPrice": {
                tours.sort(Comparator.comparing(Tour::getPrice));
                break;
            }
        }
        model.addAttribute("tour", tours);
        return "tour/tour-filters";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable("id") Long tourId, Model model) {
        User user = null;
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            user = userRepository.findById(UserID).orElse(null);
            model.addAttribute("userId", 1);
        }
        model.addAttribute("isAdmin", isAdmin);

        Tour tour = tourRepository.findById(tourId).get();
        Hotel hotel = tour.getHotel();
        List<AdditionalService> additionalServices = serviceRepository.findAll();

        model.addAttribute("additionalServices", additionalServices);
        model.addAttribute("tour", tour);
        model.addAttribute("hotel", hotel);
        model.addAttribute("user", user);
        return "tour/tour-info";
    }

    @GetMapping("edit/{id}")
    public String getTourForEdit(@PathVariable("id") Long id, Model model) {
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
        }
        model.addAttribute("isAdmin", isAdmin);

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
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
        }
        model.addAttribute("isAdmin", isAdmin);
        List<Country> countries = countryRepository.findAll();
        model.addAttribute("countries", countries);
        List<Hotel> hotels = hotelRepository.findAll();
        model.addAttribute("hotels", hotels);
        return "tour/tour-add";
    }

    @Transactional
    @PostMapping
    public String createTour(
            @RequestParam("hotelName") String hotelName,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("countryName") String countryName,
            @RequestParam("price") double price,
            @RequestParam("placeQuantity") int placeQuantity,
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            Model model
    ) {
        Country country = countryRepository.findByName(countryName).get();
        Hotel hotel = hotelRepository.findByName(hotelName).get();
        Tour createdTour = Tour.builder()
                .title(title)
                .description(description)
                .price(new BigDecimal(price))
                .placeQuantity(placeQuantity)
                .checkInDate(startDate)
                .checkOutDate(endDate)
                .country(country)
                .hotel(hotel)
                .build();
        tourRepository.save(createdTour);
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
        }
        model.addAttribute("isAdmin", isAdmin);
        return "redirect:/tours";
    }

    @Transactional
    @PutMapping("/{id}")
    public String tourUpdate(
            @PathVariable("id") Long id,
            @RequestParam("hotelName") String hotelName,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("countryName") String countryName,
            @RequestParam("price") double price,
            @RequestParam("placeQuantity") int placeQuantity,
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            Model model
    ) {
        Hotel hotel = hotelRepository.findByName(hotelName).get();
        Country country = countryRepository.findByName(countryName).get();
        Tour foundTour = tourRepository.findById(id).get();
        foundTour.setHotel(hotel);
        foundTour.setTitle(title);
        foundTour.setDescription(description);
        foundTour.setPrice(new BigDecimal(price));
        foundTour.setCountry(country);
        foundTour.setPlaceQuantity(placeQuantity);
        foundTour.setCheckInDate(startDate);
        foundTour.setCheckOutDate(endDate);
        Tour savedTour = tourRepository.save(foundTour);
        model.addAttribute("tour", savedTour);
        return "redirect:/tours";
    }

    @Transactional
    @DeleteMapping("/{id}")
    public String deleteTour(@PathVariable("id") Long id) {
        tourRepository.deleteById(id);
        return "redirect:/tours";
    }

    @GetMapping("/search")
    public String searchTour(@RequestParam("query") String query, Model model) {
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
        }
        model.addAttribute("isAdmin", isAdmin);
        filteredTours = tourRepository.findByTitleContainingOrDescriptionContaining(query, query);
        if (filteredTours.isEmpty()) {
            model.addAttribute("message", "Результатов по данному запросу не найдено.");
        } else {
            model.addAttribute("tour", filteredTours);
        }
        return "tour/tour-filters";
    }
}
