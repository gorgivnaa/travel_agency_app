package com.popytka.popytka.controller;

import com.popytka.popytka.entity.Booking;
import com.popytka.popytka.entity.User;
import com.popytka.popytka.repository.BookingRepository;
import com.popytka.popytka.repository.UserRepository;
import com.popytka.popytka.service.BookingGeneratorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

import static com.popytka.popytka.controller.MainController.UserID;
import static com.popytka.popytka.controller.MainController.isAdmin;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class BookingController {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingGeneratorService bookingGeneratorService;

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadBookingPdfById(@PathVariable("id") Long id) throws IOException {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdfBytes = bookingGeneratorService.generateBookingPdf(booking);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "booking.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public String getBookingsByUser(@PathVariable(value = "userId") Long userId, Model model) {
        User user = userRepository.findById(userId).get();
        List<Booking> bookings = bookingRepository.findByUser(user);
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
        }
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("user", user);
        model.addAttribute("bookings", bookings);
        return "bookings/bookings";
    }
}
