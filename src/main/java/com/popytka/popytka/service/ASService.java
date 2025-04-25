package com.popytka.popytka.service;

import com.popytka.popytka.controller.filter.AdditionalServiceFilter;
import com.popytka.popytka.dto.AdditionalServiceDTO;
import com.popytka.popytka.entity.AdditionalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ASService {

    Page<AdditionalService> getAdditionalServices(AdditionalServiceFilter ASFilter, Pageable pageable);

    AdditionalServiceDTO createAS(AdditionalServiceDTO additionalServiceDTO);
}
