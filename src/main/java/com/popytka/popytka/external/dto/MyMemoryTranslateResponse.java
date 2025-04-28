package com.popytka.popytka.external.dto;
import lombok.Data;

@Data
public class MyMemoryTranslateResponse {

    private ResponseData responseData;

    @Data
    public static class ResponseData {
        private String translatedText;
    }
}

