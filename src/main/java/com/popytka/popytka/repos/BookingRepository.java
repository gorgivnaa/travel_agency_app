package com.popytka.popytka.repos;

import com.popytka.popytka.models.Booking;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BookingRepository extends CrudRepository<Booking, Long> {

    List<Booking> findByUser_UserId(Long user_Id);
//    @Modifying(clearAutomatically = true)
//    @Query("INSERT INTO Booking (tour_id, user_id, service_id, hotel_id, place_quantity, check_in_date, check_out_date) VALUES (:tour_id, :user_id, :service_id, :hotel_id, :place_quantity, :check_in_date, :check_out_date)")
//    void acceptBooking(@Param("user_id") Long user_id, @Param("tour_id") Long tour_id, @Param("service_id") Long service_id, @Param("hotel_id") Long hotel_id, @Param("place_quantity") int place_quantity, @Param("check_in_date") LocalDate check_in_date, @Param("check_out_date") LocalDate check_out_date);

//   @Query("select b from Booking b where user_id = ?1")
//   List<Booking> getBookingsByUserId(Long id);

//    @Query("select tour_id from Booking where booking_id = ?1")
//    Long getTourId(Long id);
//    @Query("select user_id from Booking where booking_id = ?1")
//    Long getUserId(Long id);
//    @Query("select service_id from Booking where booking_id = ?1")
//    Long getServiceId(Long id);
//    @Query("select hotel_id from Booking where booking_id = ?1")
//    Long getHotelId(Long id);

    @Query("select b from Booking b")
    List<Booking> getAllBookings();

}
