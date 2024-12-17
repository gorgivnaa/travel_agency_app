package com.popytka.popytka.controllers;

import com.popytka.popytka.models.*;
import com.popytka.popytka.repos.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.popytka.popytka.controllers.MainController.UserID;
import static com.popytka.popytka.controllers.MainController.isAdmin;

@Controller
public class TourController {
    List<Tour> filteredTours = new ArrayList<>();
    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping("/tour")
    public String tourMain(Model model, @RequestParam(required = false) String sort, HttpSession session) {
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        List<Tour> tours = tourRepository.getTours();

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
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("iserId", 1);
        }
        model.addAttribute("tour", tours);
        model.addAttribute("isAdmin", isAdmin);
        return "tour";
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
            // Если оба параметра отсутствуют, выполните выборку всех данных
            tours = tourRepository.getToursFilter();
            filteredTours = tours;
        } else if (countryName != null && maxPrice == null) {
            tours = tourRepository.findTourByCountry(countryName);
            filteredTours = tours;
        } else if (countryName == null && maxPrice != null) {
            tours = tourRepository.findTourByPrice(maxPrice);
            filteredTours = tours;
        } else {
            // Иначе, выполните выборку данных с применением фильтров
            tours = tourRepository.findTourByFilters(countryName, maxPrice);
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
        return "tourfilters";
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
        return "tourfilters";
    }

    @GetMapping("/tourinfo/{tour_id}")
    public String showTourInfo(@PathVariable("tour_id") Long tour_id, Model model) {
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        List<Tour> tour = tourRepository.getTourById(tour_id);
        Hotel hotel = hotelRepository.getHotelByTourId(tour_id);
        User user = userRepository.getById(UserID);
        List<Service> service = serviceRepository.getServices();
        model.addAttribute("service", service);
        model.addAttribute("tour", tour);
        model.addAttribute("hotel", hotel);
        model.addAttribute("user", user);
        return "tourinfo";
    }

    @GetMapping("/tour/add")
    public String tourAdd(Model model) {
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        List<Country> countries = countryRepository.getCountries();
        model.addAttribute("country", countries);
        List<Hotel> hotels = hotelRepository.getAllHotels();
        model.addAttribute("hotel", hotels);
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        return "tour-add";
    }

    @Transactional
    @PostMapping("/tour/add")
    public String tourAddInfo(@RequestParam("hotel_name") String hotelName, @RequestParam("title") String title, @RequestParam("description") String description, @RequestParam("country_name") String countryName, @RequestParam("price") double price, @RequestParam("place_quantity") int place_quantity,
                              @RequestParam("start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                              @RequestParam("end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate, Model model) {
        tourRepository.addTour(hotelName, title, description, countryName, price, place_quantity, startDate, endDate);
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
    public List<Hotel> getHotelsByCountry(@RequestParam("country") String countryName, Model model) {
        List<Hotel> hotels = hotelRepository.getHotelsByCountry(countryName);
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
    public String addNewHotel(HttpServletRequest request, Model model, @RequestParam("new_hotel_name") String hotelName, @RequestParam("country_name") String countryName, @RequestParam("new_hotel_description") String description, @RequestParam("rate") double rating) {
        // Обработка запроса на добавление нового отеля
        // Например, установите атрибут модели, указывающий на необходимость отображения формы
        hotelRepository.addHotel(hotelName, countryName, description, rating);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @GetMapping("/tour/edit/{tour_id}")
    public String tourEdit(@PathVariable("tour_id") Long tourId, Model model) {
        List<Tour> tour = tourRepository.getTourById(tourId);
        List<Country> country = countryRepository.getCountries();
        List<Hotel> hotel = hotelRepository.getAllHotels();
        model.addAttribute("tour", tour);
        model.addAttribute("country", country);
        model.addAttribute("hotel", hotel);
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("iserId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        return "tour-edit";
    }

    @Transactional
    @PostMapping("/tour/edit/{tour_id}")
    public String tourUpdate(@PathVariable("tour_id") Long id,
                             @RequestParam("hotel_name") String hotelName,
                             @RequestParam("title") String title,
                             @RequestParam("description") String description,
                             @RequestParam("country_name") String countryName,
                             @RequestParam("price") double price,
                             @RequestParam("place_quantity") int place_quantity,
                             @RequestParam("start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                             @RequestParam("end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                             Model model) {
        Long hotel_id = hotelRepository.getHotelsIdByName(hotelName);
        tourRepository.updateTour(id, hotel_id, title, description, countryName, price, place_quantity, startDate, endDate);
        List<Tour> tour = tourRepository.getTourById(id);
        model.addAttribute("tour", tour);
        return "redirect:/tour";
    }

    @Transactional
    @PostMapping("/tour/delete/{tour_id}")
    public String tourDelete(@PathVariable("tour_id") Long id) {
        tourRepository.deleteTour(id);
        return "redirect:/tour";
    }

    @GetMapping("/search")
    public String searchTour(@RequestParam("query") String query, Model model) {
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        filteredTours = tourRepository.searchTour(query);
        if (filteredTours.isEmpty()) {
            model.addAttribute("message", "Результатов по данному запросу не найдено.");
        } else {
            model.addAttribute("tour", filteredTours);
        }
        model.addAttribute("isAdmin", isAdmin);
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("iserId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        return "tourfilters";
    }

}
