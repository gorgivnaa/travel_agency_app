package com.popytka.popytka.external.service.impl.country.generator;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DateGenerator extends BaseGenerator {

    public LocalDate generateStartDate() {
        return LocalDate.now().plusDays(random.nextLong(5) + 1);
    }

    public LocalDate generateEndDate(LocalDate startDate) {
        return startDate.plusDays(random.nextInt(6) + 1);
    }
}
