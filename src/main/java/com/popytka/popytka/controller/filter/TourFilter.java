package com.popytka.popytka.controller.filter;

import com.popytka.popytka.controller.filter.specification.NumberSpecification;
import com.popytka.popytka.controller.filter.specification.StringSpecification;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.entity.Tour_;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

public record TourFilter(
        String title,
        BigDecimal price
) implements StringSpecification<Tour>,
        NumberSpecification<Tour> {

    public Specification<Tour> toSpecification() {
        return Specification.allOf(
                titleSpec(),
                priceSpec()
        );
    }

    private Specification<Tour> titleSpec() {
        return buildSpecFromString(List.of(Tour_.TITLE), title);
    }

    private Specification<Tour> priceSpec() {
        return buildSpecFromNumber(List.of(Tour_.PRICE), price);
    }
}
