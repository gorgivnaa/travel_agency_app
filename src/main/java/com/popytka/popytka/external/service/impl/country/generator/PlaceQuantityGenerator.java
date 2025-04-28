package com.popytka.popytka.external.service.impl.country.generator;

import org.springframework.stereotype.Component;

@Component
public class PlaceQuantityGenerator extends BaseGenerator {

    public int generatePlaceQuantity(){
        return random.nextInt(10) + 1;
    }
}
