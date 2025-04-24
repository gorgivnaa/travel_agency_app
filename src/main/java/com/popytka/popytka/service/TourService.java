package com.popytka.popytka.service;

import com.popytka.popytka.controller.filter.TourFilter;
import com.popytka.popytka.entity.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TourService {

    Page<Tour> getAllServices(TourFilter filter, Pageable pageable);

    Optional<Tour> getById(Long id);
}
