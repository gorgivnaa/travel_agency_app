package com.popytka.popytka.repository;

import com.popytka.popytka.entity.Tag;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.entity.TourTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TourTagRepository extends JpaRepository<TourTag, Long> {

    @Query("""
        SELECT t
        FROM Tour t
        JOIN t.tags tt
        WHERE tt.tag.id IN :tagIds
        GROUP BY t.id
        ORDER BY COUNT(tt.tag.id) DESC
    """)
    List<Tour> findToursByTagIdsOrderedBySimilarity(@Param("tagIds") List<Long> tagIds);

    Set<Tag> findByTourId(Long tourId);

    void deleteByTagIdAndTourId(Long tagId, Long tourId);
}