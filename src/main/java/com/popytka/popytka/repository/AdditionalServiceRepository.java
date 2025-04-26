package com.popytka.popytka.repository;

import com.popytka.popytka.entity.AdditionalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AdditionalServiceRepository extends JpaRepository<AdditionalService, Long>,
        JpaSpecificationExecutor<AdditionalService> {

    Optional<AdditionalService> findByName(String serviceName);
}
