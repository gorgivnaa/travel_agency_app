package com.popytka.popytka.repos;

import com.popytka.popytka.models.Tour;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TourRepository extends CrudRepository<Tour, Long> {
    @Query("select t from Tour as t")
    List<Tour> getTours();

    @Query("select t from Tour as t")
    List<Tour> getToursFilter();

    @Query("SELECT t FROM Tour t JOIN Country c ON c.country_id = t.country_id WHERE c.country_name = ?1")
    List<Tour> findTourByCountry(String country_name);

    @Query("SELECT t FROM Tour t WHERE t.price <= ?1")
    List<Tour> findTourByPrice(BigDecimal price);

    @Query("SELECT t FROM Tour t JOIN Country c ON c.country_id = t.country_id WHERE c.country_name = ?1 AND t.price <= ?2")
    List<Tour> findTourByFilters(String country_name, BigDecimal price);

    @Query("SELECT t FROM Tour t WHERE t.tour_id = ?1")
    List<Tour> getTourById(Long tour_id);

    @Query("SELECT tour_id FROM Tour t WHERE t.title = ?1")
    Long getToursIdByTitle(String title);

    @Modifying(clearAutomatically = true)
    @Query("INSERT INTO Tour (hotel_id, title, description, country_id, price, place_quantity, check_in_date, check_out_date) VALUES ((SELECT h.hotel_id FROM Hotel h WHERE h.name = :hotelName), :title, :description, (SELECT c.country_id FROM Country c WHERE c.country_name = :countryName), :price, :place_quantity, :check_in_date, :check_out_date)")
    void addTour(@Param("hotelName") String hotelName, @Param("title") String title, @Param("description") String description, @Param("countryName") String countryName, @Param("price") double price, @Param("place_quantity") int place_quantity, @Param("check_in_date") LocalDate check_in_date,
                 @Param("check_out_date") LocalDate check_out_date);

    @Modifying(clearAutomatically = true)
    @Query("update Tour set hotel_id = :hotel_id, title = :title, description = :description, country_id = (select country_id from Country where country_name = :countryName), price = :price, place_quantity = :place_quantity, check_in_date = :startDate, check_out_date = :endDate where tour_id = :id")
    void updateTour(@Param("id") Long id,
                    @Param("hotel_id") Long hotel_id,
                    @Param("title") String title,
                    @Param("description") String description,
                    @Param("countryName") String countryName,
                    @Param("price") double price,
                    @Param("place_quantity") int place_quantity,
                    @Param("startDate") LocalDate startDate,
                    @Param("endDate") LocalDate endDate);

    @Modifying(clearAutomatically = true)
    @Query("delete from Tour where tour_id = ?1")
    void deleteTour(Long id);

    @Query("SELECT check_in_date FROM Tour t WHERE t.tour_id = ?1")
    LocalDate getCheckInDate(Long id);
    @Query("SELECT check_out_date FROM Tour t WHERE t.tour_id = ?1")
    LocalDate getCheckOutDate(Long id);

    @Query("select hotel_id from Tour where tour_id = ?1")
    Long getHotelIdByTourId(Long id);

    @Query("SELECT t FROM Tour as t WHERE t.title LIKE %:query% or t.description LIKE %:query%")
    List<Tour> searchTour(@Param("query") String query);

    @Query("select t from Tour as t where t.title = ?1")
    Tour getTourByTitle(String title);
//    @Query("select t from Tour where tour_id =(select o.tour_id from Orders as o where order_id = :order_id)")
//    Tour getTourByOrderId(@Param("order_id") Long order_id);


}