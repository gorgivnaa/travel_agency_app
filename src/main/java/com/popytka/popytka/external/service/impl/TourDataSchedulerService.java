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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class TourDataSchedulerService {

    private final List<TourDataLoader> loaders;
    private final TourRepository tourRepository;
    private final HotelRepository hotelRepository;
    private final CountryRepository countryRepository;

    //    @Scheduled(fixedRate = 3 * 60 * 1000)
    @Scheduled(cron = "0 0 12 * * *", zone = "Europe/Moscow")
    public void loadItaly() {
        loadData("Италия");
    }

    //    @Scheduled(fixedRate = 5 * 60 * 1000)
    @Scheduled(cron = "0 0 13 * * *", zone = "Europe/Moscow")
    public void loadRussia() {
        loadData("Россия");
    }

    //    @Scheduled(fixedRate = 7 * 60 * 1000)
    @Scheduled(cron = "0 0 14 * * *", zone = "Europe/Moscow")
    public void loadFrance() {
        loadData("Франция");
    }

    //    @Scheduled(fixedRate = 9 * 60 * 1000)
    @Scheduled(cron = "0 0 15 * * *", zone = "Europe/Moscow")
    public void loadSpain() {
        loadData("Испания");
    }

    //    @Scheduled(fixedRate = 11 * 60 * 1000)
    @Scheduled(cron = "0 0 16 * * *", zone = "Europe/Moscow")
    public void loadGreece() {
        loadData("Греция");
    }

    @Transactional
    private void loadData(String countryName) {
        List<Tour> tours = tourRepository.findAll();
        List<Hotel> hotels = hotelRepository.findAll();
        List<Country> countries = countryRepository.findAll();
        TourDataLoader loader = loaders.stream()
                .filter(tourDataLoader -> tourDataLoader.getCountryName().equals(countryName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Загрузчика для страны " + countryName + " не существует!"));
        loader.loadDataFromCountry(tours, countries, hotels);
    }
}
