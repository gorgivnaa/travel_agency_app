package com.popytka.popytka.service;

import com.popytka.popytka.dto.statistic.CountryStatDto;
import com.popytka.popytka.dto.statistic.MonthlyManagerStatDto;
import com.popytka.popytka.dto.statistic.TourStatDto;

import java.util.List;

public interface StatisticService {

    List<TourStatDto> getTourStatistics();

    List<MonthlyManagerStatDto> getManagerStatistics(Long managerId);

    List<CountryStatDto> getCountriesStatistic();
}
