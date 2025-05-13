package com.popytka.popytka.service.impl;

import com.popytka.popytka.controller.filter.TourFilter;
import com.popytka.popytka.entity.Booking;
import com.popytka.popytka.entity.Country;
import com.popytka.popytka.entity.Hotel;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.entity.User;
import com.popytka.popytka.repository.BookingRepository;
import com.popytka.popytka.repository.TourRepository;
import com.popytka.popytka.repository.UserRepository;
import com.popytka.popytka.service.TourService;
import com.popytka.popytka.util.SeasonUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Page<Tour> getFilteredTours(TourFilter filter, Pageable pageable) {
        Specification<Tour> spec = filter.toSpecification();
        return tourRepository.findAll(spec, pageable);
    }

    @Override
    public List<Tour> getAllToursForOrders() {
        return tourRepository.findAll();
    }

    @Override
    public List<Tour> getAllTours(Long id) {
        List<Integer> months = SeasonUtil.getMonthsIntForCurrentSeason();
        if (id == null) {
            return tourRepository.findPopularToursBySeason(months);
        }

        User user = userRepository.findById(id).get();
        List<Booking> userBookings = bookingRepository.findByUser(user);
        if (userBookings.isEmpty()) {
            return tourRepository.findPopularToursBySeason(months);
        }

        return tourRepository.findNearestAvgPriceTours(id);
    }

    @Override
    public List<Tour> getSearchedTours(String titleOrDescription) {
        return tourRepository.findByTitleContainingOrDescriptionContaining(titleOrDescription, titleOrDescription);
    }

    @Override
    public Optional<Tour> getById(Long id) {
        return tourRepository.findById(id);
    }

    @Override
    public Tour createTour(Tour tour, Country country, Hotel hotel) {
        Tour createdTour = Tour.builder()
                .title(tour.getTitle())
                .description(tour.getDescription())
                .price(tour.getPrice())
                .placeQuantity(tour.getPlaceQuantity())
                .checkInDate(tour.getCheckInDate())
                .checkOutDate(tour.getCheckOutDate())
                .country(country)
                .hotel(hotel)
                .build();
        return tourRepository.save(createdTour);
    }

    @Override
    public Tour updateTour(Long id, Tour updatedTourDTO, Country country, Hotel hotel) {
        Tour foundTour = tourRepository.findById(id).get();
        foundTour.setHotel(hotel);
        foundTour.setTitle(updatedTourDTO.getTitle());
        foundTour.setDescription(updatedTourDTO.getDescription());
        foundTour.setPrice(updatedTourDTO.getPrice());
        foundTour.setCountry(country);
        foundTour.setPlaceQuantity(updatedTourDTO.getPlaceQuantity());
        foundTour.setCheckInDate(updatedTourDTO.getCheckInDate());
        foundTour.setCheckOutDate(updatedTourDTO.getCheckOutDate());
        return tourRepository.save(foundTour);
    }
}
