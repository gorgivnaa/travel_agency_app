package com.popytka.popytka.repos;

import com.popytka.popytka.models.Hotel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HotelRepository extends CrudRepository<Hotel, Long> {
    @Query("select h from Hotel as h")
    List<Hotel> getAllHotels();

    @Query("SELECT h FROM Hotel h WHERE h.country_id = (SELECT c.country_id from Country c where c.country_name = :countryName)")
    List<Hotel> getHotelsByCountry(@Param("countryName") String countryName);

    @Query("select hotel_id from Hotel h where h.name = ?1")
    Long getHotelsIdByName(String name);

    @Modifying(clearAutomatically = true)
    @Query("INSERT INTO Hotel (name, country_id, rating, description) VALUES (:hotelName, (SELECT c.country_id FROM Country c WHERE c.country_name = :countryName), :rating, :description)")
    void addHotel(@Param("hotelName") String hotelName, @Param("countryName") String countryName, @Param("description") String description, @Param("rating") double rating);

    @Query("select h from Hotel as h where hotel_id=(select t.hotel_id from Tour as t where tour_id = :tour_id)")
    Hotel getHotelByTourId(@Param("tour_id") Long tour_id);

    @Query("select h from Hotel as h where hotel_id=:hotel_id")
    Hotel getHotelById(@Param("hotel_id") Long hotel_id);
}
