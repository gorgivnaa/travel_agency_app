package com.popytka.popytka.external.service.impl.country.generator;

import com.popytka.popytka.entity.Hotel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HotelGenerator extends BaseGenerator {

    public Hotel getRandomHotel(List<Hotel> hotels) {
        return hotels.get(random.nextInt(hotels.size()));
    }
}
