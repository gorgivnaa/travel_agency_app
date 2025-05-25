package com.popytka.popytka.service.impl;

import com.popytka.popytka.entity.Tag;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.repository.TourTagRepository;
import com.popytka.popytka.service.TourTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
class TourTagServiceImpl implements TourTagService {

    private final TourTagRepository tourTagRepository;

    @Override
    public List<Tour> getToursByTags(List<Tag> tags) {
        return CollectionUtils.isEmpty(tags)
                ? null
                : getByTags(tags);
    }

    private List<Tour> getByTags(List<Tag> tags) {
        List<Long> tagIds = tags.stream()
                .map(Tag::getId)
                .toList();

        return tourTagRepository.findToursByTagIdsOrderedBySimilarity(tagIds);
    }
}
