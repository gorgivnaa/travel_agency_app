package com.popytka.popytka.controller;

import com.popytka.popytka.config.security.CustomUserDetails;
import com.popytka.popytka.entity.AdditionalService;
import com.popytka.popytka.entity.Booking;
import com.popytka.popytka.entity.Hotel;
import com.popytka.popytka.entity.Order;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.entity.User;
import com.popytka.popytka.repository.BookingRepository;
import com.popytka.popytka.repository.OrderRepository;
import com.popytka.popytka.repository.TourRepository;
import com.popytka.popytka.service.OrderService;
import com.popytka.popytka.service.TourService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class OrderController {

    private final TourService tourService;
    private final OrderService orderService;
    private final TourRepository tourRepository;
    private final OrderRepository orderRepository;
    private final BookingRepository bookingRepository;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String getAllOrders(Model model, Authentication authentication) {
        List<Order> orders;
        List<Tour> tours = tourService.getAllToursForOrders();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("MANAGER"))) {
            Long managerId = ((CustomUserDetails) authentication.getPrincipal()).getId();
            orders = orderService.getOrdersByManager(managerId);
        } else {
            orders = orderService.getAllOrders();
        }
        if (orders.isEmpty()) {
            model.addAttribute("message", "У вас на данный момент нет заявок на туры.");
        }
        model.addAttribute("orders", orders);
        model.addAttribute("tours", tours);
        return "order/orders";
    }

    @Transactional
    @PostMapping
    public String addOrder(
            Model model,
            @RequestParam("tourTitle") String tourTitle,
            @RequestParam("numberOfPeople") int numberOfPeople,
            @RequestParam("serviceName") String serviceName,
            @RequestParam("orderDate") LocalDateTime orderDate,
            Authentication authentication
    ) {
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        Optional<Order> orderOptional = orderService.createOrder(
                tourTitle,
                numberOfPeople,
                serviceName,
                orderDate,
                userId
        );
        if (orderOptional.isEmpty()) {
            model.addAttribute("successMessage", "Ошибка при отправке заявки.");
            return "tour/tour-info";
        }

        Order order = orderOptional.get();
        model.addAttribute("tour", order.getTour());
        model.addAttribute("hotel", order.getTour().getHotel());
        model.addAttribute("user", order.getUser());
        model.addAttribute("successMessage", "Заявка успешно отправлена.");
        return "tour/tour-info";
    }

    @Transactional
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String deleteOrder(@PathVariable("id") Long id) {
        orderRepository.deleteById(id);
        return "redirect:/orders";
    }

    @Transactional
    @PostMapping("/{id}/accept")
    public String acceptOrder(@PathVariable("id") Long id) {
        BigDecimal totalPrice;
        Order order = orderRepository.findById(id).get();
        User user = order.getUser();
        Tour tour = order.getTour();
        AdditionalService additionalService = order.getAdditionalService();
        Hotel hotel = tour.getHotel();
        int placeQuantity = order.getPlaceQuantity();
        LocalDate checkInDate = tour.getCheckInDate();
        LocalDate checkOutDate = tour.getCheckOutDate();
        if (additionalService == null) {
            totalPrice = tour.getPrice();
        } else {
            totalPrice = tour.getPrice().add(additionalService.getPrice());
        }
        Booking booking = Booking.builder()
                .tour(tour)
                .user(user)
                .hotel(hotel)
                .additionalService(additionalService)
                .placeQuantity(placeQuantity)
                .price(totalPrice)
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .build();
        bookingRepository.save(booking);
        orderRepository.deleteById(id);
        tour.setPlaceQuantity(tour.getPlaceQuantity() - placeQuantity);
        tourRepository.save(tour);
        return "redirect:/orders";
    }
}
