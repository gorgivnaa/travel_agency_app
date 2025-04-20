package com.popytka.popytka.repos;

import com.popytka.popytka.models.AdditionalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceRepository extends JpaRepository<AdditionalService, Long> {

    @Query("select s from Service as s")
    List<AdditionalService> getServices();

    @Query("select s from Service as s where s.service_name = ?1")
    AdditionalService getServiceByName(String serviceName);

    @Query("""
            SELECT s FROM Service as s
            WHERE s.service_name LIKE %:service_name%
            """)
    List<AdditionalService> searchServiceByName(@Param("service_name") String serviceName);

    @Modifying(clearAutomatically = true)
    @Query("""
            INSERT INTO Service (service_name, price, description)
            VALUES (:service_name, :price, :service_description)
            """)
    void addService(
            @Param("service_name") String serviceName,
            @Param("service_description") String serviceDescription,
            @Param("price") double price
    );
}
