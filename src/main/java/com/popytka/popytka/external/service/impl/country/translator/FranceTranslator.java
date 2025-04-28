package com.popytka.popytka.external.service.impl.country.translator;

import com.popytka.popytka.external.dto.ActivityDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class FranceTranslator extends TranslatorServiceBase {

    public ActivityDTO translateActivityDTO(ActivityDTO activityDTO, String languageFromCode) {
       return this.translateActivityDTO(activityDTO, "fr");
    }
}
