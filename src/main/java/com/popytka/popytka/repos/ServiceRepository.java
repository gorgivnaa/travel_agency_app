package com.popytka.popytka.repos;

import com.popytka.popytka.models.Service;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceRepository  extends CrudRepository<Service, Long> {
    @Query("select s from Service as s")
    List<Service> getServices();

    @Query("select s from Service as s where s.service_name = ?1")
    Service getServiceByName(String serviceName);

    @Query("SELECT s FROM Service as s WHERE s.service_name LIKE %:query%")
    List<Service> searchService(@Param("query") String query);

    @Modifying(clearAutomatically = true)
    @Query("INSERT INTO Service (service_name, price) VALUES (:service_name, :price)")
    void addService(@Param("service_name") String service_name, @Param("price") double price);


//    @Query("select s from Service where service_id =(select o.service_id from Orders as o where order_id = :order_id)")
//    Service getServiceByOrderId(@Param("order_id") Long order_id);
}
