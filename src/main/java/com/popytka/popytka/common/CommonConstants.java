package com.popytka.popytka.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class CommonConstants {

    public static final String LIKE_CONDITION_FORMAT = "%%%s%%";
    public static final char CHAR_ASTERISK_DELIMITER = '*';
    public static final String STRING_ASTERISK_DELIMITER = "*";

    public static final String NUMBER_WITH_PARAMETER_REGEX = "^([a-zA-Z]{2})?(\\d{1,19}(\\.\\d+)?|\\.\\d+)$";
    public static final byte PREFIX_DATA_GROUP_NUMBER = 1;
    public static final byte DATA_GROUP_NUMBER = 2;
}
