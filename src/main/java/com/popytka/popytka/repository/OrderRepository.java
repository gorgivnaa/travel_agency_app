package com.popytka.popytka.repository;

import com.popytka.popytka.dto.statistic.CountryStatDto;
import com.popytka.popytka.dto.statistic.TourStatDto;
import com.popytka.popytka.entity.Order;
import com.popytka.popytka.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByTourIn(List<Tour> tours);

    Long countByTour(Tour tour);

    @Query("""
            SELECT new com.popytka.popytka.dto.statistic.TourStatDto(o.tour.title, COUNT(o))
            FROM Order o
            GROUP BY o.tour.title
            """)
    List<TourStatDto> countOrdersByTour();

    @Query(value = """
    SELECT
        DATE_FORMAT(o.order_date, '%m/%Y') AS month,
        COUNT(*) AS count
    FROM orders o
    JOIN tours t ON o.tour_id = t.id
    JOIN manager_tours mt ON mt.tour_id = t.id
    WHERE mt.manager_id = :managerId
    GROUP BY month
    ORDER BY MIN(o.order_date)
    """, nativeQuery = true)
    List<Object[]> getRawMonthlyStatsByManager(@Param("managerId") Long managerId);

    @Query("""
            SELECT new com.popytka.popytka.dto.statistic.CountryStatDto(o.tour.country.name, COUNT(o))
            FROM Order o
            GROUP BY o.tour.country.name
            """)
    List<CountryStatDto> countOrdersByCountry();
}
