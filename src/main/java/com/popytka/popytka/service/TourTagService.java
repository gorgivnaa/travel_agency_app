package com.popytka.popytka.service;

import com.popytka.popytka.entity.Tour;

import java.util.List;

public interface TourTagService {

    List<Tour> getToursByTags(String concatenatedTags);
}
