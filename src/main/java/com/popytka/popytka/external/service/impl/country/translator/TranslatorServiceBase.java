package com.popytka.popytka.external.service.impl.country.translator;

import com.popytka.popytka.external.client.MyMemoryTranslateClient;
import com.popytka.popytka.external.dto.ActivityDTO;
import com.popytka.popytka.external.dto.MyMemoryTranslateResponse;
import com.popytka.popytka.external.service.TranslatorService;
import com.popytka.popytka.external.util.DescriptionUtil;
import lombok.AllArgsConstructor;

@AllArgsConstructor
abstract class TranslatorServiceBase implements TranslatorService {

    private final MyMemoryTranslateClient myMemoryTranslateClient;

    @Override
    public ActivityDTO translateActivityDTO(ActivityDTO activityDTO) throws InterruptedException {
        String languages = "%s|ru".formatted(getLanguageCode());
        MyMemoryTranslateResponse titleTranslation = myMemoryTranslateClient.translate(activityDTO.getName(), languages);
        String processedDescription = DescriptionUtil.processDescription(activityDTO.getDescription());
        Thread.sleep(2000);
        MyMemoryTranslateResponse descriptionTranslation = myMemoryTranslateClient.translate(processedDescription, languages);
        Thread.sleep(2000);
        activityDTO.setName(titleTranslation.getResponseData().getTranslatedText());
        activityDTO.setDescription(descriptionTranslation.getResponseData().getTranslatedText());
        return activityDTO;
    }

    public abstract String getLanguageCode();
}
