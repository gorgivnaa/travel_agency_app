package com.popytka.popytka.external.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class TourDataSchedulerService {

    private final TourDataLoadManager manager;

    @Scheduled(fixedRate = 3 * 60 * 1000)
    // @Scheduled(cron = "0 0 12 * * *", zone = "Europe/Moscow")
    public void scheduledLoad() {
        manager.loadAllDataAsync();
    }
}
