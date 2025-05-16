package com.popytka.popytka.controller;

import com.popytka.popytka.entity.Order;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.service.OrderService;
import com.popytka.popytka.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class StatisticController {

    private final TourService tourService;
    private final OrderService orderService;

    @GetMapping
    public String showStatistics(Model model) {
        List<Tour> tours = tourService.getAllToursForOrders();
        List<Order> orders = orderService.getAllOrders();

        model.addAttribute("tours", tours);
        model.addAttribute("orders", orders);
        return "statistics/all-statistic";
    }
}
