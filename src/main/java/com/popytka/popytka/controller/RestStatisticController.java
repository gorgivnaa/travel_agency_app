package com.popytka.popytka.controller;

import com.popytka.popytka.config.security.CustomUserDetails;
import com.popytka.popytka.dto.statistic.CountryStatDto;
import com.popytka.popytka.dto.statistic.MonthlyManagerStatDto;
import com.popytka.popytka.dto.statistic.TourStatDto;
import com.popytka.popytka.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class RestStatisticController {

    private final StatisticService statisticService;

    @GetMapping("/tours")
    public List<TourStatDto> getTourStats() {
        return statisticService.getTourStatistics();
    }

    @GetMapping("/managers")
    public List<MonthlyManagerStatDto> getManagerMonthStats(Authentication authentication){
        Long managerId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        return statisticService.getManagerStatistics(managerId);
    }

    @GetMapping("/countries")
    public List<CountryStatDto> getCountryStatistics() {
        return statisticService.getCountriesStatistic();
    }
}
