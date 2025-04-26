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

    Page<Tour> getAllServices(TourFilter filter, Pageable pageable);

    List<Tour> getAllServices();

    Optional<Tour> getById(Long id);

    Tour createTour(Tour tour, Country country, Hotel hotel);

    Tour updateTour(Long id, Tour tour, Country country, Hotel hotel);
}
