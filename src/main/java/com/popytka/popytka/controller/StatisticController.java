package com.popytka.popytka.controller;

import com.popytka.popytka.entity.Order;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.service.TourService;
import com.popytka.popytka.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static com.popytka.popytka.controller.MainController.UserID;
import static com.popytka.popytka.controller.MainController.isAdmin;

@Controller
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class StatisticController {

    private final TourService tourService;
    private final OrderService orderService;

    @GetMapping
    public String showStatistics(Model model){
        List<Tour> tours = tourService.getAllToursForOrders();
        List<Order> orders = orderService.getAllOrders();

        model.addAttribute("userId", UserID == null ? 0 : 1);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("tours", tours);
        model .addAttribute("orders", orders);
        return "statistics/all-statistic";
    }
}
