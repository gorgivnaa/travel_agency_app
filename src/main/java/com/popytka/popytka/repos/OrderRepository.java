package com.popytka.popytka.repos;

import com.popytka.popytka.models.Order;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
    @Query("select o from Orders o")
    List<Order> getAllOrders();

//    @Modifying(clearAutomatically = true)
//    @Query("INSERT INTO Orders (user_id, tour_id, service_id, order_date, place_quantity) VALUES (:user_id, :tour_id, :service_id, :order_date, :numberOfPeople)")
//    void addOrder(@Param("user_id") Long user_id, @Param("tour_id") Long tour_id, @Param("service_id") Long service_id, @Param("order_date") LocalDateTime order_date, @Param("numberOfPeople") int numberOfPeople);

//    @Query("select user_id from Orders where order_id = ?1")
//    Long getUserId(Long id);
//
//    @Query("select tour_id from Orders where order_id = ?1")
//    Long getTourId(Long id);
//
//    @Query("select service_id from Orders where order_id = ?1")
//    Long getServiceId(Long id);

    @Query("select place_quantity from Orders where order_id = ?1")
    int getPlaceQua(Long id);

    @Modifying(clearAutomatically = true)
    @Query("delete from Orders where order_id = ?1")
    void deleteOrder(Long id);

    @Query("Select o from Orders as o where order_id = :id_order")
    Order getOrderById(@Param("id_order") Long id);
}