package com.popytka.popytka.external.service.impl.country.loader;

import com.popytka.popytka.entity.Country;
import com.popytka.popytka.entity.Hotel;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.external.client.AmadeusFeignClient;
import com.popytka.popytka.external.dto.ActivityDTO;
import com.popytka.popytka.external.service.TourDataLoader;
import com.popytka.popytka.external.service.TranslatorService;
import com.popytka.popytka.external.service.impl.country.generator.DateGenerator;
import com.popytka.popytka.external.service.impl.country.generator.HotelGenerator;
import com.popytka.popytka.external.service.impl.country.generator.PlaceQuantityGenerator;
import com.popytka.popytka.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class TourDataLoaderBase implements TourDataLoader {

    private final DateGenerator dateGenerator;
    private final HotelGenerator hotelGenerator;
    private final TourRepository tourRepository;
    private final TranslatorService translatorService;
    private final AmadeusFeignClient amadeusFeignClient;
    private final PlaceQuantityGenerator placeQuantityGenerator;

    public abstract String getCountryName();

    protected abstract double getLatitude();

    protected abstract double getLongitude();

    protected abstract int getRadius();

    @Override
    public void loadDataFromCountry(
            List<Tour> tours,
            List<Country> countries,
            List<Hotel> hotels
    ) {
        Country country = getCountry(countries, getCountryName());
        List<Hotel> countryHotels = getHotelsByCountry(hotels, country);

        var response = amadeusFeignClient.getActivities(
                getLatitude(),
                getLongitude(),
                getRadius()
        );
        List<ActivityDTO> activityDTOs = response.getData();
        log.info("Загрузка из страны '{}' началась!", getCountryName());

        saveTours(
                activityDTOs,
                tours,
                country,
                countryHotels
        );
        log.info("Загрузка из страны '{}' завершилась!", getCountryName());
    }

    private void saveTours(
            List<ActivityDTO> activityDTOs,
            List<Tour> tours,
            Country country,
            List<Hotel> hotels
    ) {
        List<Tour> createdTours = activityDTOs.stream()
                .filter(this::isCorrectActivityDTO)
                .map(activity -> {
                    try {
                        return mapToTour(activity, country, hotels);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(tour -> isNewTour(tour, tours))
                .toList();

        log.info(createdTours.toString());
        tourRepository.saveAll(createdTours);
    }

    private Tour mapToTour(ActivityDTO activityDTO, Country country, List<Hotel> hotels) throws InterruptedException {
        ActivityDTO translatedActivity = translatorService.translateActivityDTO(activityDTO);
        int placeQuantity = placeQuantityGenerator.generatePlaceQuantity();
        Hotel hotel = hotelGenerator.getRandomHotel(hotels);
        BigDecimal price = translatedActivity.getPrice().getPriceInBYN();
        LocalDate checkInDate = dateGenerator.generateStartDate();
        LocalDate checkOutDate = dateGenerator.generateEndDate(checkInDate);

        return Tour.builder()
                .title(translatedActivity.getName())
                .description(translatedActivity.getDescription())
                .placeQuantity(placeQuantity)
                .price(price)
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .country(country)
                .hotel(hotel)
                .build();
    }

    private boolean isNewTour(Tour tour, List<Tour> tours) {
        return !tours.stream()
                .map(Tour::getTitle)
                .toList()
                .contains(tour.getTitle());
    }

    private boolean isCorrectActivityDTO(ActivityDTO activityDTO) {
        return (activityDTO != null
                && activityDTO.getName() != null
                && activityDTO.getDescription() != null
                && activityDTO.getPrice() != null
        );
    }

    protected Country getCountry(List<Country> countries, String countryName) {
        return countries.stream()
                .filter(c -> c.getName().equals(countryName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Страна не найдена: " + countryName));
    }

    protected List<Hotel> getHotelsByCountry(List<Hotel> hotels, Country country) {
        return hotels.stream()
                .filter(h -> h.getCountry().equals(country))
                .toList();
    }
}
