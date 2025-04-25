package com.popytka.popytka.service.impl;

import com.popytka.popytka.controller.filter.AdditionalServiceFilter;
import com.popytka.popytka.dto.AdditionalServiceDTO;
import com.popytka.popytka.entity.AdditionalService;
import com.popytka.popytka.repository.AdditionalServiceRepository;
import com.popytka.popytka.service.ASService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ASServiceImpl implements ASService {

    private final AdditionalServiceRepository additionalServiceRepository;

    @Override
    public Page<AdditionalService> getAdditionalServices(AdditionalServiceFilter ASFilter, Pageable pageable) {
        Specification<AdditionalService> spec = ASFilter.toSpecification();
        return additionalServiceRepository.findAll(spec, pageable);
    }

    @Override
    public AdditionalServiceDTO createAS(AdditionalServiceDTO additionalServiceDTO) {
        AdditionalService createdAdditionalService = AdditionalService.builder()
                .name(additionalServiceDTO.getName())
                .description(additionalServiceDTO.getDescription())
                .price(additionalServiceDTO.getPrice())
                .build();
        AdditionalService savedAS = additionalServiceRepository.save(createdAdditionalService);
        return AdditionalServiceDTO.builder()
                .name(savedAS.getName())
                .description(savedAS.getDescription())
                .price(savedAS.getPrice())
                .build();
    }
}
