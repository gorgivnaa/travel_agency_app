package com.popytka.popytka.external.service;

import com.popytka.popytka.entity.Country;
import com.popytka.popytka.entity.Hotel;
import com.popytka.popytka.entity.Tour;

import java.util.List;

public interface TourDataLoader {

    void loadAllData(
            List<Tour> tours,
            List<Country> countries,
            List<Hotel> hotels
    );
}
