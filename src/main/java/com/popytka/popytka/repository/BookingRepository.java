package com.popytka.popytka.repository;

import com.popytka.popytka.entity.Booking;
import com.popytka.popytka.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);
}
