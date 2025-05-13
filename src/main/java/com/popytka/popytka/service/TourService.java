package com.popytka.popytka.service;

import com.popytka.popytka.controller.filter.TourFilter;
import com.popytka.popytka.entity.Country;
import com.popytka.popytka.entity.Hotel;
import com.popytka.popytka.entity.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TourService {

    Page<Tour> getFilteredTours(TourFilter filter, Pageable pageable);

    List<Tour> getAllToursForOrders();

    List<Tour> getAllTours(Long userId);

    List<Tour> getSearchedTours(String titleOrDescription);

    Optional<Tour> getById(Long id);

    Tour createTour(Tour tour, Country country, Hotel hotel);

    Tour updateTour(Long id, Tour tour, Country country, Hotel hotel);
}
