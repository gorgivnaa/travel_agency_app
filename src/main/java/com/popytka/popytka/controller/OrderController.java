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
import com.popytka.popytka.repository.UserRepository;
import com.popytka.popytka.service.ASService;
import com.popytka.popytka.service.TourService;
import com.popytka.popytka.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
import java.util.List;

import static com.popytka.popytka.controller.MainController.isAdmin;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class OrderController {

    private final UserRepository userRepository;
    private final TourRepository tourRepository;
    private final OrderRepository orderRepository;
    private final BookingRepository bookingRepository;
    private final ASService asService;
    private final OrderService orderService;
    private final TourService tourService;

    @GetMapping
    public String getAllOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        List<Tour> tours = tourService.getAllToursForOrders();

        model.addAttribute("isAdmin", isAdmin);
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
        Tour tour = tourRepository.findByTitle(tourTitle).orElse(null);
        if (tour == null) {
            model.addAttribute("successMessage", "Ошибка при отправке заявки.");
        } else {
            Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
            Hotel hotel = tour.getHotel();
            User user = userRepository.findById(userId).get();
            AdditionalService additionalService = asService.getByName(serviceName);
            Order order = Order.builder()
                    .orderDateTime(orderDate)
                    .placeQuantity(numberOfPeople)
                    .user(user)
                    .additionalService(additionalService)
                    .tour(tour)
                    .build();
            orderRepository.save(order);
            model.addAttribute("tour", tour);
            model.addAttribute("hotel", hotel);
            model.addAttribute("user", user);
            model.addAttribute("successMessage", "Заявка успешно отправлена.");
        }
        return "tour/tour-info";
    }

    @Transactional
    @DeleteMapping("/{id}")
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
