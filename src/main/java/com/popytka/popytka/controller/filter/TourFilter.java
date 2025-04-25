package com.popytka.popytka.controller.filter;

import com.popytka.popytka.controller.filter.specification.NumberSpecification;
import com.popytka.popytka.controller.filter.specification.StringSpecification;
import com.popytka.popytka.entity.Country_;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.entity.Tour_;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@Getter
@Setter
public class TourFilter implements StringSpecification<Tour>,
        NumberSpecification<Tour> {

    private String title;
    private String countryName;
    private List<String> price;

    public Specification<Tour> toSpecification() {
        return Specification.allOf(
                titleSpec(),
                priceSpec(),
                countrySpec()
        );
    }

    private Specification<Tour> titleSpec() {
        return buildSpecFromString(List.of(Tour_.TITLE), title);
    }

    private Specification<Tour> priceSpec() {
        return buildSpecFromNumbers(List.of(Tour_.PRICE), price);
    }

    private Specification<Tour> countrySpec() {
        return buildSpecFromString(List.of(Tour_.COUNTRY, Country_.NAME), countryName);
    }
}
