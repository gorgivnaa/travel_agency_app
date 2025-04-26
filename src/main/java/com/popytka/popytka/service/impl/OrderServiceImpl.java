package com.popytka.popytka.service.impl;

import com.popytka.popytka.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.popytka.popytka.entity.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
