package com.popytka.popytka.external.service.impl.country.translator;

import com.popytka.popytka.external.client.MyMemoryTranslateClient;
import com.popytka.popytka.external.dto.ActivityDTO;
import com.popytka.popytka.external.dto.MyMemoryTranslateResponse;
import com.popytka.popytka.external.service.TranslatorService;
import com.popytka.popytka.external.util.DescriptionUtil;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class TranslatorServiceBase implements TranslatorService {

    private MyMemoryTranslateClient myMemoryTranslateClient;

    @Override
    public ActivityDTO translateActivityDTO(ActivityDTO activityDTO, String languageCode){
        String languages = "%s|ru".formatted(languageCode);
        MyMemoryTranslateResponse titleTranslation = myMemoryTranslateClient.translate(activityDTO.getName(), languages);
        String processedDescription = DescriptionUtil.processDescription(activityDTO.getDescription());
        MyMemoryTranslateResponse descriptionTranslation = myMemoryTranslateClient.translate(processedDescription, languages);
        activityDTO.setName(titleTranslation.getResponseData().getTranslatedText());
        activityDTO.setDescription(descriptionTranslation.getResponseData().getTranslatedText());
        return activityDTO;
    }
}
