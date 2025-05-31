package com.popytka.popytka.service.impl;

import com.popytka.popytka.dto.statistic.CountryStatDto;
import com.popytka.popytka.dto.statistic.MonthlyManagerStatDto;
import com.popytka.popytka.dto.statistic.TourStatDto;
import com.popytka.popytka.repository.OrderRepository;
import com.popytka.popytka.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class StatisticServiceImpl implements StatisticService {

    private final OrderRepository orderRepository;

    @Override
    public List<TourStatDto> getTourStatistics() {
        return orderRepository.countOrdersByTour();
    }

    @Override
    public List<MonthlyManagerStatDto> getManagerStatistics(Long managerId) {
        List<Object[]> rows = orderRepository.getRawMonthlyStatsByManager(managerId);
        return rows.stream()
                .map(row -> new MonthlyManagerStatDto((String) row[0], ((Number) row[1]).longValue()))
                .toList();
    }


    @Override
    public List<CountryStatDto> getCountriesStatistic() {
        return orderRepository.countOrdersByCountry();
    }
}
