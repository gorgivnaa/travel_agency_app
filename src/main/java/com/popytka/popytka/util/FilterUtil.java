package com.popytka.popytka.util;

import com.popytka.popytka.controller.filter.TourFilter;
import org.springframework.stereotype.Component;

@Component
public class FilterUtil {

    public void addPriceFilter(TourFilter filter) {
        if (filter.getPrice() == null || filter.getPrice().isEmpty()) {
            return;
        }
        filter.getPrice().set(0, "le" + filter.getPrice().get(0));
    }
}
