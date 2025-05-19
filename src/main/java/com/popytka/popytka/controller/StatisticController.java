package com.popytka.popytka.controller;

import com.popytka.popytka.config.security.CustomUserDetails;
import com.popytka.popytka.entity.Order;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.service.OrderService;
import com.popytka.popytka.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String showStatistics(Model model, Authentication authentication) {
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        List<Tour> tours = tourService.getAllToursForOrders();
        List<Order> orders = orderService.getAllOrders();
        List<Order> employeeOrders = orderService.getOrdersByManager(userId);

        model.addAttribute("tours", tours);
        model.addAttribute("orders", orders);
        model.addAttribute("managerOrders", employeeOrders);

        return "statistics/all-statistic";
    }
}
