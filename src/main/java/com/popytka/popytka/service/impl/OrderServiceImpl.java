package com.popytka.popytka.service.impl;

import com.popytka.popytka.entity.AdditionalService;
import com.popytka.popytka.entity.Order;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.entity.User;
import com.popytka.popytka.entity.enums.OrderStatus;
import com.popytka.popytka.repository.OrderRepository;
import com.popytka.popytka.service.ASService;
import com.popytka.popytka.service.ManagerTourService;
import com.popytka.popytka.service.OrderService;
import com.popytka.popytka.service.TourService;
import com.popytka.popytka.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class OrderServiceImpl implements OrderService {

    private final ASService asService;
    private final TourService tourService;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final ManagerTourService managerTourService;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByManager(Long managerId) {
        List<Tour> toursByManager = managerTourService.getToursByManagerId(managerId);
        return orderRepository.findByTourIn(toursByManager);
    }

    @Override
    public Optional<Order> createOrder(
            String tourTitle,
            int numberOfPeople,
            String serviceName,
            LocalDateTime orderDate,
            Long userId
    ) {
        Optional<Tour> tourOptional = tourService.getByTitle(tourTitle);
        if (tourOptional.isEmpty()) {
            return Optional.empty();
        }
        Tour tour = tourOptional.get();
        User user = userService.findById(userId).get();
        AdditionalService additionalService = asService.getByName(serviceName);
        Order order = Order.builder()
                .orderDateTime(orderDate)
                .placeQuantity(numberOfPeople)
                .user(user)
                .additionalService(additionalService)
                .tour(tour)
                .status(OrderStatus.PROCESSING)
                .build();
        Order savedOrder = orderRepository.save(order);
        return Optional.of(savedOrder);
    }
}
