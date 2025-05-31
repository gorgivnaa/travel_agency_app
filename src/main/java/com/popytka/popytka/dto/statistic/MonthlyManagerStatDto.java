package com.popytka.popytka.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MonthlyManagerStatDto {

    private String month;
    private Long count;
}
