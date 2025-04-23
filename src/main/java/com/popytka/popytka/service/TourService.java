package com.popytka.popytka.service;

import com.popytka.popytka.dto.TourResponse;

public interface TourService {

    TourResponse getAllServices();

    TourResponse getById(Long id);
}
