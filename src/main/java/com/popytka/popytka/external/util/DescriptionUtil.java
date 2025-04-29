package com.popytka.popytka.external.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DescriptionUtil {

    public String processDescription(String description) {
        if (description == null) {
            return "";
        }

        String cleaned = description
                .replaceAll("\n", "")
                .replaceAll("———", "")
                .replaceAll("<[^>]+>", "")
                .trim();

        if (cleaned.length() <= 500) {
            return cleaned;
        }

        String truncated = cleaned.substring(0, 500);
        int lastDotIndex = truncated.lastIndexOf('.');
        return lastDotIndex != -1
                ? truncated.substring(0, lastDotIndex + 1).trim()
                : truncated.trim();
    }
}
