package com.popytka.popytka.service;

import com.popytka.popytka.dto.ManagerAssignmentDto;
import com.popytka.popytka.entity.Tour;

import java.util.List;

public interface ManagerTourService {

    List<ManagerAssignmentDto> getManagersWithAssignmentStatus(Long tourId);

    void saveManagersForTour(Tour tour, List<Long> managerIds);

    List<Tour> getToursByManagerId(Long managerId);
}
