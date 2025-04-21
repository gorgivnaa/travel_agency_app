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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.popytka.popytka.controller.MainController.UserID;
import static com.popytka.popytka.controller.MainController.isAdmin;

@Controller
@RequiredArgsConstructor
public class TourController {

    private List<Tour> filteredTours = new ArrayList<>();

    private final UserRepository userRepository;
    private final TourRepository tourRepository;
    private final HotelRepository hotelRepository;
    private final CountryRepository countryRepository;
    private final ServiceRepository serviceRepository;

    @GetMapping("/tours")
    public String tourMain(Model model, @RequestParam(required = false) String sort) {
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

    @GetMapping("/tourfilters")
    public String tourFilters(Model model, @RequestParam(required = false) String countryName, @RequestParam(required = false) BigDecimal maxPrice) {
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
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
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("iserId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }

        model.addAttribute("tour", filteredTours);
        return "tour/tourfilters";
    }

    @GetMapping("/tourfilters/{sort}")
    public String tourFiltersSort(@PathVariable(value = "sort") String sort, Model model) {
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
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
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("iserId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        model.addAttribute("tour", tours);
        return "tour/tourfilters";
    }

    @GetMapping("/tourinfo/{tourId}")
    public String showTourInfo(@PathVariable("tourId") Long tourId, Model model) {
        User user = null;
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            user = userRepository.findById(UserID).orElse(null);
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }

        Tour tour = tourRepository.findById(tourId).get();
        Hotel hotel = tour.getHotel();
        List<AdditionalService> additionalServices = serviceRepository.findAll();

        model.addAttribute("additionalServices", additionalServices);
        model.addAttribute("tour", tour);
        model.addAttribute("hotel", hotel);
        model.addAttribute("user", user);
        return "tour/tourinfo";
    }

    @GetMapping("/tours/add")
    public String tourAdd(Model model) {
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        List<Country> countries = countryRepository.findAll();
        model.addAttribute("country", countries);
        List<Hotel> hotels = hotelRepository.findAll();
        model.addAttribute("hotel", hotels);
        return "tour/tour-add";
    }

    @Transactional
    @PostMapping("/tours/add")
    public String tourAddInfo(
            @RequestParam("hotel_name") String hotelName,
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
            model.addAttribute("isAdmin", isAdmin);
        }
        return "redirect:/tour";
    }

    @GetMapping("/hotel")
    @ResponseBody
    public List<Hotel> getHotelsByCountry(@RequestParam("countryName") String countryName, Model model) {
        Country country = countryRepository.findByName(countryName).get();
        List<Hotel> hotels = hotelRepository.findByCountry(country);
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        return hotels;
    }

    @Transactional
    @PostMapping("/hotel/add")
    public String addNewHotel(
            HttpServletRequest request,
            @RequestParam("new_hotel_name") String hotelName,
            @RequestParam("country_name") String countryName,
            @RequestParam("new_hotel_description") String description,
            @RequestParam("rate") double rating
    ) {
        Country country = countryRepository.findByName(countryName).get();
        Hotel createdHotel = Hotel.builder()
                .name(hotelName)
                .description(description)
                .rating(rating)
                .country(country)
                .build();
        hotelRepository.save(createdHotel);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @GetMapping("/tours/edit/{id}")
    public String tourEdit(@PathVariable("id") Long id, Model model) {
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }

        Tour tour = tourRepository.findById(id).get();
        List<Country> country = countryRepository.findAll();
        List<Hotel> hotel = hotelRepository.findAll();
        model.addAttribute("tour", tour);
        model.addAttribute("country", country);
        model.addAttribute("hotel", hotel);
        return "tour/tour-edit";
    }

    @Transactional
    @PostMapping("/tours/edit/{id}")
    public String tourUpdate(
            @PathVariable("id") Long id,
            @RequestParam("hotel_name") String hotelName,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("country_name") String countryName,
            @RequestParam("price") double price,
            @RequestParam("place_quantity") int placeQuantity,
            @RequestParam("start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
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
        return "redirect:/tour";
    }

    @Transactional
    @DeleteMapping("/tours/{id}")
    public String deleteTour(@PathVariable("id") Long id) {
        tourRepository.deleteById(id);
        return "redirect:/tour";
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
        return "tour/tourfilters";
    }
}
