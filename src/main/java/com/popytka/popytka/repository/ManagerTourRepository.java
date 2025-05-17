package com.popytka.popytka.repository;

import com.popytka.popytka.entity.ManagerTour;
import com.popytka.popytka.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManagerTourRepository extends JpaRepository<ManagerTour, Long> {

    List<ManagerTour> findByTourId(Long id);

    List<ManagerTour> findByTour(Tour tour);

    void deleteByManagerIdAndTourId(Long managerId, Long tourId);
}