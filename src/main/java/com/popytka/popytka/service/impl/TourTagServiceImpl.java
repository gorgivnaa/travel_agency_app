package com.popytka.popytka.service.impl;

import com.popytka.popytka.dto.TagAssignmentDto;
import com.popytka.popytka.entity.Tag;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.entity.TourTag;
import com.popytka.popytka.repository.TagRepository;
import com.popytka.popytka.repository.TourTagRepository;
import com.popytka.popytka.service.TourService;
import com.popytka.popytka.service.TourTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public List<TagAssignmentDto> getTagsWithAssignmentStatus(Long tourId) {
        List<Tag> allTags = tagRepository.findAll();
        if (tourId == null) {
            return allTags.stream()
                    .map(tag -> new TagAssignmentDto(tag, false))
                    .collect(Collectors.toList());
        }

        Set<Tag> assignedManagers = tourTagRepository.findByTourId(tourId).stream()
                .map(TourTag::getTag)
                .collect(Collectors.toSet());

        return allTags.stream()
                .map(tag -> new TagAssignmentDto(
                        tag,
                        assignedManagers.contains(tag)))
                .collect(Collectors.toList());
    }

    @Override
    public void saveTagsForTour(Tour tour, List<Long> tagIds) {
        Set<Long> currentTagIds = tourTagRepository.findByTourId(tour.getId()).stream()
                .map(tt -> tt.getTag().getId())
                .collect(Collectors.toSet());

        Set<Long> newTagIds = new HashSet<>(tagIds);
        newTagIds.removeAll(currentTagIds);

        Set<Long> removedTagIds = new HashSet<>(currentTagIds);
        tagIds.forEach(removedTagIds::remove);

        newTagIds.forEach(tagId -> {
            Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new RuntimeException("Tag not found"));
            tourTagRepository.save(
                    TourTag.builder()
                            .tag(tag)
                            .tour(tour)
                            .build()
            );
        });

        removedTagIds.forEach(tagId -> tourTagRepository.deleteByTagIdAndTourId(
                tagId, tour.getId()
        ));
    }
}
