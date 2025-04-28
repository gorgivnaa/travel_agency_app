package com.popytka.popytka.external.service.impl.country.loader;

import com.popytka.popytka.external.client.AmadeusFeignClient;
import com.popytka.popytka.external.service.TranslatorService;
import com.popytka.popytka.external.service.impl.country.generator.DateGenerator;
import com.popytka.popytka.external.service.impl.country.generator.HotelGenerator;
import com.popytka.popytka.external.service.impl.country.generator.PlaceQuantityGenerator;
import com.popytka.popytka.repository.TourRepository;
import org.springframework.stereotype.Service;

@Service
class ItalyTourDataLoad extends TourDataLoaderBase {

    private static final String LANGUAGE_CODE = "it";
    private static final String COUNTRY_NAME = "Италия";
    private static final double LATITUDE = 41.902689;
    private static final double LONGITUDE = 12.496176;
    private static final int RADIUS = 1;

    public ItalyTourDataLoad(
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
    protected String getCountryName() {
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

    @Override
    protected String getLanguageCode() {
        return LANGUAGE_CODE;
    }
}
