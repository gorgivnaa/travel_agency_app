package com.popytka.popytka.service.impl;

import com.popytka.popytka.dto.ManagerAssignmentDto;
import com.popytka.popytka.entity.ManagerTour;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.entity.User;
import com.popytka.popytka.repository.ManagerTourRepository;
import com.popytka.popytka.service.ManagerTourService;
import com.popytka.popytka.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class ManagerTourServiceImpl implements ManagerTourService {

    private final UserService userService;
    private final ManagerTourRepository managerTourRepository;

    @Override
    public List<ManagerAssignmentDto> getManagersWithAssignmentStatus(Long tourId) {
        List<User> allManagers = userService.getByRoleName("MANAGER");
        if (tourId == null) {
            return allManagers.stream()
                    .map(manager -> new ManagerAssignmentDto(manager, false))
                    .collect(Collectors.toList());
        }


        Set<User> assignedManagers = managerTourRepository.findByTourId(tourId).stream()
                .map(ManagerTour::getManager)
                .collect(Collectors.toSet());

        return allManagers.stream()
                .map(manager -> new ManagerAssignmentDto(
                        manager,
                        assignedManagers.contains(manager)))
                .collect(Collectors.toList());
    }

    @Override
    public void saveManagersForTour(Tour tour, List<Long> managerIds) {
        Set<Long> currentManagerIds = managerTourRepository.findByTour(tour).stream()
                .map(mt -> mt.getManager().getId())
                .collect(Collectors.toSet());

        Set<Long> newManagerIds = new HashSet<>(managerIds);
        newManagerIds.removeAll(currentManagerIds);

        Set<Long> removedManagerIds = new HashSet<>(currentManagerIds);
        managerIds.forEach(removedManagerIds::remove);

        newManagerIds.forEach(managerId -> {
            User manager = userService.findById(managerId)
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
            managerTourRepository.save(
                    ManagerTour.builder()
                            .manager(manager)
                            .tour(tour)
                            .build()
            );
        });

        removedManagerIds.forEach(managerId -> managerTourRepository.deleteByManagerIdAndTourId(
                managerId, tour.getId()
        ));
    }

    @Override
    public List<Tour> getToursByManagerId(Long managerId) {
        return managerTourRepository.findByManagerId(managerId).stream()
                .map(ManagerTour::getTour)
                .toList();
    }
}
