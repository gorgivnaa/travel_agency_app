package com.popytka.popytka.service.impl;

import com.popytka.popytka.entity.Tag;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.repository.TagRepository;
import com.popytka.popytka.repository.TourTagRepository;
import com.popytka.popytka.service.TourService;
import com.popytka.popytka.service.TourTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
class TourTagServiceImpl implements TourTagService {

    private final TourService tourService;
    private final TagRepository tagRepository;
    private final TourTagRepository tourTagRepository;

    @Override
    public List<Tour> getToursByTags(String concatenatedTags) {
        List<String> tags = Arrays.asList(concatenatedTags.split(","));
        List<Tour> allTours = tourService.getAllTours(null);
        if (CollectionUtils.isEmpty(tags)) {
            return allTours;
        }
        List<Tour> tours = getByTags(tags);
        return tours.isEmpty()
                ? allTours
                : tours;
    }

    private List<Tour> getByTags(List<String> tags) {
        List<Long> tagIds = tags.stream()
                .flatMap(name -> tagRepository.findByName(name).stream())
                .map(Tag::getId)
                .toList();

        return tourTagRepository.findToursByTagIdsOrderedBySimilarity(tagIds);
    }
}
