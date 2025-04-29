package com.popytka.popytka.external.service.impl.country.loader;

import com.popytka.popytka.external.client.AmadeusFeignClient;
import com.popytka.popytka.external.service.TranslatorService;
import com.popytka.popytka.external.service.impl.country.generator.DateGenerator;
import com.popytka.popytka.external.service.impl.country.generator.HotelGenerator;
import com.popytka.popytka.external.service.impl.country.generator.PlaceQuantityGenerator;
import com.popytka.popytka.repository.TourRepository;
import org.springframework.stereotype.Service;

@Service
class FranceTourDataLoad extends TourDataLoaderBase {

    private static final String COUNTRY_NAME = "Франция";
    private static final double LATITUDE = 45.764957;
    private static final double LONGITUDE = 4.826932;
    private static final int RADIUS = 10;

    public FranceTourDataLoad(
            DateGenerator dateGenerator,
            HotelGenerator hotelGenerator,
            TourRepository tourRepository,
            TranslatorService translatorService,
            AmadeusFeignClient amadeusFeignClient,
            PlaceQuantityGenerator placeQuantityGenerator
    ) {
        super(
                dateGenerator,
                hotelGenerator,
                tourRepository,
                translatorService,
                amadeusFeignClient,
                placeQuantityGenerator
        );
    }

    @Override
    public String getCountryName() {
        return COUNTRY_NAME;
    }

    @Override
    protected double getLatitude() {
        return LATITUDE;
    }


    @Override
    protected double getLongitude() {
        return LONGITUDE;
    }


    @Override
    protected int getRadius() {
        return RADIUS;
    }
}
