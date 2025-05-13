package com.popytka.popytka.util;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class SeasonUtil {

    public static List<Integer> getMonthsIntForCurrentSeason() {
        Month month = LocalDate.now().getMonth();
        return switch (month) {
            case DECEMBER, JANUARY, FEBRUARY -> List.of(12, 1, 2);
            case MARCH, APRIL, MAY -> List.of(3, 4, 5);
            case JUNE, JULY, AUGUST -> List.of(6, 7, 8);
            case SEPTEMBER, OCTOBER, NOVEMBER -> List.of(9, 10, 11);
        };
    }
}
