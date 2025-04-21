package com.popytka.popytka.repository;

import com.popytka.popytka.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}