package com.popytka.popytka.service.impl;

import com.popytka.popytka.controller.filter.TourFilter;
import com.popytka.popytka.dto.TourDTO;
import com.popytka.popytka.entity.Country;
import com.popytka.popytka.entity.Hotel;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.repository.TourRepository;
import com.popytka.popytka.service.TourService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;

    @Override
    public Page<Tour> getAllServices(TourFilter filter, Pageable pageable) {
        Specification<Tour> spec = filter.toSpecification();
        return tourRepository.findAll(spec, pageable);
    }

    @Override
    public Optional<Tour> getById(Long id) {
        return tourRepository.findById(id);
    }

    @Override
    public TourDTO createTour(TourDTO tourDTO, Country country, Hotel hotel){
        Tour createdTour = Tour.builder()
                .title(tourDTO.getTitle())
                .description(tourDTO.getDescription())
                .price(tourDTO.getPrice())
                .placeQuantity(tourDTO.getPlaceQuantity())
                .checkInDate(tourDTO.getStartDate())
                .checkOutDate(tourDTO.getEndDate())
                .country(country)
                .hotel(hotel)
                .build();
        Tour savedTour = tourRepository.save(createdTour);
        return TourDTO.builder()
                .title(savedTour.getTitle())
                .description(savedTour.getDescription())
                .price(savedTour.getPrice())
                .countryName(savedTour.getCountry().getName())
                .hotelName(savedTour.getHotel().getName())
                .placeQuantity(savedTour.getPlaceQuantity())
                .startDate(savedTour.getCheckInDate())
                .endDate(savedTour.getCheckOutDate())
                .build();
    }

    @Override
    public TourDTO updateTour(Long id, TourDTO updatedTourDTO, Country country, Hotel hotel){
        Tour foundTour = tourRepository.findById(id).get();
        foundTour.setHotel(hotel);
        foundTour.setTitle(updatedTourDTO.getTitle());
        foundTour.setDescription(updatedTourDTO.getDescription());
        foundTour.setPrice(updatedTourDTO.getPrice());
        foundTour.setCountry(country);
        foundTour.setPlaceQuantity(updatedTourDTO.getPlaceQuantity());
        foundTour.setCheckInDate(updatedTourDTO.getStartDate());
        foundTour.setCheckOutDate(updatedTourDTO.getEndDate());
        Tour savedTour = tourRepository.save(foundTour);
        return TourDTO.builder()
                .title(savedTour.getTitle())
                .description(savedTour.getDescription())
                .price(savedTour.getPrice())
                .countryName(savedTour.getCountry().getName())
                .hotelName(savedTour.getHotel().getName())
                .placeQuantity(savedTour.getPlaceQuantity())
                .startDate(savedTour.getCheckInDate())
                .endDate(savedTour.getCheckOutDate())
                .build();
    }
}
