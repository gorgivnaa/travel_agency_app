package com.popytka.popytka.repository;

import com.popytka.popytka.models.Country;
import com.popytka.popytka.models.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByCountry(Country country);

    Optional<Hotel> findByName(String name);
}
