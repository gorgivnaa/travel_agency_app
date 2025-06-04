package com.popytka.popytka.service.impl;

import ch.qos.logback.core.util.StringUtil;
import com.popytka.popytka.controller.filter.AdditionalServiceFilter;
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
    public AdditionalService createAS(AdditionalService additionalService) {
        AdditionalService createdAdditionalService = AdditionalService.builder()
                .name(additionalService.getName())
                .description(additionalService.getDescription())
                .price(additionalService.getPrice())
                .build();
        return additionalServiceRepository.save(createdAdditionalService);
    }

    @Override
    public AdditionalService getByName(String additionalServiceName) {
        return additionalServiceRepository.findByName(additionalServiceName).orElse(null);
    }

    @Override
    public void deleteAS(Long additionalServiceId) {
        if (additionalServiceId == null) {
            return;
        }
        additionalServiceRepository.deleteById(additionalServiceId);
    }

    @Override
    public AdditionalService updateAdditionalService(Long additionalServiceId, AdditionalService updatedAS) {
        AdditionalService foundAdditionalService = additionalServiceRepository.findById(additionalServiceId)
                .orElseThrow(() -> new RuntimeException("Дополнительной услуги с id = " + additionalServiceId + " не найдено!"));

        foundAdditionalService.setName(updatedAS.getName());
        foundAdditionalService.setDescription(updatedAS.getDescription());
        foundAdditionalService.setPrice(updatedAS.getPrice());
        return additionalServiceRepository.save(foundAdditionalService);
    }

    @Override
    public Page<AdditionalService> searchAdditionalServices(String nameOrDescription, Pageable pageable) {
        return StringUtil.isNullOrEmpty(nameOrDescription)
                ? Page.empty()
                : additionalServiceRepository.findByNameContainingOrDescriptionContaining(nameOrDescription, nameOrDescription, pageable);
    }
}
