package com.popytka.popytka.repository;

import com.popytka.popytka.models.AdditionalService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<AdditionalService, Long> {

    Optional<AdditionalService> findByName(String serviceName);

    List<AdditionalService> findByNameContaining(String serviceName);
}
