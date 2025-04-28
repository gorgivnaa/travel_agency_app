package com.popytka.popytka.external.service;

import com.popytka.popytka.external.dto.ActivityDTO;

public interface TranslatorService {

    ActivityDTO translateActivityDTO(ActivityDTO activityDTO, String languageFromCode);
}
