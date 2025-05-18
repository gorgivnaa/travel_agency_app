package com.popytka.popytka.service;

import com.popytka.popytka.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    List<Order> getAllOrders();

    List<Order> getOrdersByManager(Long managerId);

    Optional<Order> createOrder(
            String tourTitle,
            int numberOfPeople,
            String serviceName,
            LocalDateTime orderDate,
            Long userId
    );
}
