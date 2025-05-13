package com.popytka.popytka.repository;

import com.popytka.popytka.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TourRepository extends JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour> {

    List<Tour> findByTitleContainingOrDescriptionContaining(String title, String description);

    Optional<Tour> findByTitle(String title);

    @Query(nativeQuery = true, value = """
            SELECT t.* 
            FROM tours t 
            LEFT JOIN orders o ON o.tour_id = t.id 
            GROUP BY t.id 
            ORDER BY 
                CASE 
                    WHEN EXTRACT(MONTH FROM t.check_in_date) IN (:seasonMonths) THEN 0
                    ELSE 1
                END,
                COUNT(o.id) DESC
            """)
    List<Tour> findPopularToursBySeason(@Param("seasonMonths") List<Integer> seasonMonths);

    @Query(nativeQuery = true, value = """
    SELECT t.* 
    FROM tours t 
    ORDER BY 
        CASE 
            WHEN ( 
                SELECT COUNT(*) FROM bookings WHERE user_id = :userId 
            ) > 0 THEN ABS(t.price - ( 
                SELECT AVG(b.price) FROM bookings b WHERE b.user_id = :userId 
            )) 
            ELSE NULL 
        END 
    """)
    List<Tour> findNearestAvgPriceTours(
            @Param ("userId") Long userId
    );
}