package com.popytka.popytka.service.impl;

import com.popytka.popytka.controller.filter.TourFilter;
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
}
