package com.popytka.popytka.repository;

import com.popytka.popytka.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TourRepository extends JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour> {

    List<Tour> findByCountryName(String countryName);

    List<Tour> findByPriceLessThanEqual(BigDecimal price);

    List<Tour> findByCountryNameAndPriceLessThanEqual(String countryName, BigDecimal price);

    List<Tour> findByTitleContainingOrDescriptionContaining(String title, String description);

    Optional<Tour> findByTitle(String title);
}