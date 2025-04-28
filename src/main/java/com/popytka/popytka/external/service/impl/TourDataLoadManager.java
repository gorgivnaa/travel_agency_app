package com.popytka.popytka.external.service.impl;

import com.popytka.popytka.entity.Country;
import com.popytka.popytka.entity.Hotel;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.external.service.TourDataLoader;
import com.popytka.popytka.repository.CountryRepository;
import com.popytka.popytka.repository.HotelRepository;
import com.popytka.popytka.repository.TourRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class TourDataLoadManager {

    private final List<TourDataLoader> loaders;
    private final TourRepository tourRepository;
    private final HotelRepository hotelRepository;
    private final CountryRepository countryRepository;

    @Transactional
    public void loadAllDataAsync() {
        List<Tour> tours = tourRepository.findAll();
        List<Hotel> hotels = hotelRepository.findAll();
        List<Country> countries = countryRepository.findAll();
        loaders.forEach(loader -> loader.loadAllData(tours, countries, hotels));
    }
}
