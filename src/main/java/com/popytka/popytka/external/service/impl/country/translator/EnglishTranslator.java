package com.popytka.popytka.external.service.impl.country.translator;

import com.popytka.popytka.external.client.MyMemoryTranslateClient;
import org.springframework.stereotype.Service;

@Service
class EnglishTranslator extends TranslatorServiceBase {

    private static final String LANGUAGE_CODE = "en";

    EnglishTranslator(MyMemoryTranslateClient myMemoryTranslateClient) {
        super(myMemoryTranslateClient);
    }

    @Override
    public String getLanguageCode() {
        return LANGUAGE_CODE;
    }
}
