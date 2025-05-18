package com.popytka.popytka.repository;

import com.popytka.popytka.entity.Order;
import com.popytka.popytka.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByTourIn(List<Tour> tours);
}