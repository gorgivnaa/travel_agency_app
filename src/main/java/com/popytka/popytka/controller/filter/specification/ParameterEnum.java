package com.popytka.popytka.controller.filter.specification;

/*
 * GT: Greater Than , >
 * GE: Greater than or Equivalent with , >=
 * LT: Less than, <
 * LE: Less than or Equivalent with, <=
 * EQ: Equivalent with, ==
 * NE: Not Equivalent with, /=
 */
public enum ParameterEnum {
    GT,
    GE,
    LT,
    LE,
    EQ,
    NE;

    public static ParameterEnum fromString(final String text) {
        for (final ParameterEnum fieldsEnum : ParameterEnum.values()) {
            if (fieldsEnum.toString().equalsIgnoreCase(text)) {
                return fieldsEnum;
            }
        }
        throw new RuntimeException("Неправильно введены параметры поиска!");
    }
}
