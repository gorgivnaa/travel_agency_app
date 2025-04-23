package com.popytka.popytka.controller;

import com.popytka.popytka.entity.Country;
import com.popytka.popytka.entity.Hotel;
import com.popytka.popytka.repository.CountryRepository;
import com.popytka.popytka.repository.HotelRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/hotels")
public class HotelController {

    private final HotelRepository hotelRepository;
    private final CountryRepository countryRepository;

    @ResponseBody
    @GetMapping("/{countryName}")
    public ResponseEntity<Object> getHotelsByCountry(@PathVariable("countryName") String countryName) {
        Country country = countryRepository.findByName(countryName).get();
        return ResponseEntity.ok(hotelRepository.findByCountry(country));
    }

    @Transactional
    @PostMapping("/add")
    public String addNewHotel(
            HttpServletRequest request,
            @RequestParam("newHotelName") String hotelName,
            @RequestParam("hotelCountryName") String hotelCountryName,
            @RequestParam("newHotelDescription") String description,
            @RequestParam("rate") double rating
    ) {
        Country country = countryRepository.findByName(hotelCountryName).get();
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
}
