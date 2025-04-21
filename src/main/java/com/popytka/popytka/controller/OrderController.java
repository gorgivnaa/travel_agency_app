package com.popytka.popytka.controller;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.popytka.popytka.entity.AdditionalService;
import com.popytka.popytka.entity.Booking;
import com.popytka.popytka.entity.Hotel;
import com.popytka.popytka.entity.Order;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.entity.User;
import com.popytka.popytka.repository.BookingRepository;
import com.popytka.popytka.repository.OrderRepository;
import com.popytka.popytka.repository.ServiceRepository;
import com.popytka.popytka.repository.TourRepository;
import com.popytka.popytka.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.popytka.popytka.controller.MainController.UserID;
import static com.popytka.popytka.controller.MainController.isAdmin;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final UserRepository userRepository;
    private final TourRepository tourRepository;
    private final OrderRepository orderRepository;
    private final ServiceRepository serviceRepository;
    private final BookingRepository bookingRepository;

    @GetMapping("/orders")
    public String showOrders(Model model) {
        List<Order> order = orderRepository.findAll();
        List<Tour> tours = tourRepository.findAll();
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
        }
        model.addAttribute("order", order);
        model.addAttribute("tour", tours);
        model.addAttribute("isAdmin", isAdmin);
        return "orders-new";
    }

    @Transactional
    @PostMapping("/orders/add")
    public String addOrder(
            Model model,
            @RequestParam("tourTitle") String tourTitle,
            @RequestParam("numberOfPeople") int numberOfPeople,
            @RequestParam("phoneNumber") String phone,
            @RequestParam("serviceName") String serviceName,
            @RequestParam("orderDate") LocalDateTime orderDate
    ) {
        Tour tour = tourRepository.findByTitle(tourTitle).orElse(null);
        Hotel hotel = tour.getHotel();
        User user = userRepository.findByPhone(phone).get();
        AdditionalService additionalService = serviceRepository.findByName(serviceName).get();
        Order order = Order.builder()
                .orderDateTime(orderDate)
                .placeQuantity(numberOfPeople)
                .user(user)
                .additionalService(additionalService)
                .tour(tour)
                .build();
        orderRepository.save(order);
        if (tour != null) {
            model.addAttribute("successMessage", "Заявка успешно отправлена.");
        } else {
            model.addAttribute("successMessage", "Ошибка при отправке заявки.");
        }
        model.addAttribute("tour", tour);
        model.addAttribute("hotel", hotel);
        model.addAttribute("user", user);
        return "tour/tourinfo";
    }

    @Transactional
    @PostMapping("/orders/delete/{order_id}")
    public String orderDelete(@PathVariable("order_id") Long id) {
        orderRepository.deleteById(id);
        return "redirect:/orders";
    }

    @Transactional
    @PostMapping("/orders/accept/{order_id}")
    public String orderAccept(@PathVariable("order_id") Long id) {
        BigDecimal totalPrice;
        User user = orderRepository.findById(id).get().getUser();
        Tour tour = orderRepository.findById(id).get().getTour();
        AdditionalService additionalService = orderRepository.findById(id).get().getAdditionalService();
        Hotel hotel = tour.getHotel();
        int place_qua = orderRepository.findById(id).get().getPlaceQuantity();
        LocalDate check_in_date = tourRepository.findById(tour.getId()).get().getCheckInDate();
        LocalDate check_out_date = tourRepository.findById(tour.getId()).get().getCheckOutDate();
        if (additionalService == null)
            totalPrice = tour.getPrice();
        else
            totalPrice = tour.getPrice().add(additionalService.getPrice());
        Booking booking = Booking.builder()
                .tour(tour)
                .user(user)
                .hotel(hotel)
                .additionalService(additionalService)
                .placeQuantity(place_qua)
                .price(totalPrice)
                .checkInDate(check_in_date)
                .checkOutDate(check_out_date)
                .build();
        bookingRepository.save(booking);
        orderRepository.deleteById(id);
        tour.setPlaceQuantity(tour.getPlaceQuantity() - place_qua);
        tourRepository.save(tour);
        return "redirect:/orders";
    }

    @GetMapping("/orderinfo/{order_id}")
    public String showOrderInfo(@PathVariable("order_id") Long order_id, Model model) {
        if (UserID == null) {
            model.addAttribute("userId", 0);
        } else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        Order order = orderRepository.findById(order_id).get();
        Tour tour = orderRepository.findById(order_id).get().getTour();
        User user = orderRepository.findById(order_id).get().getUser();
        List<AdditionalService> additionalService = serviceRepository.findAll();
        model.addAttribute("order", order);
        model.addAttribute("service", additionalService);
        model.addAttribute("tour", tour);
        model.addAttribute("user", user);
        return "orderinfo";
    }

    public byte[] generateBookingPdf(Booking booking) throws IOException, DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setInitialLeading(16);

            BaseFont baseFont = BaseFont.createFont("src/main/resources/fonts/font.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(baseFont, 12);

            document.open();

            Hotel hotel = booking.getHotel();
            Tour tour = booking.getTour();
            User user = booking.getUser();
            document.add(new Paragraph("" + tour.getTitle(), font));
            document.add(new Paragraph("---------------------", font));
            document.add(new Paragraph("Имя: " + user.getFirstName(), font));
            document.add(new Paragraph("Фамилия: " + user.getLastName(), font));
            document.add(new Paragraph("Количество мест: " + booking.getPlaceQuantity(), font));
            document.add(new Paragraph("---------------------", font));
            document.add(new Paragraph("Отель: " + hotel.getName(), font));

            AdditionalService additionalService = booking.getAdditionalService();
            if (additionalService != null) {
                document.add(new Paragraph("Дополнительные услуги: " + additionalService.getName(), font));
            } else {
                document.add(new Paragraph("", font));
            }


            document.add(new Paragraph("Дата прибытия: " + booking.getCheckInDate(), font));
            document.add(new Paragraph("Дата возвращения: " + booking.getCheckOutDate(), font));
            document.add(new Paragraph("Стоимость: " + booking.getPrice(), font));
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new IOException("Error creating PDF file", e);
        }

        return baos.toByteArray();
    }

    @GetMapping("/bookings/download/{bookingId}")
    public ResponseEntity<byte[]> downloadBookingPdf(@PathVariable("bookingId") Long bookingId) throws IOException, DocumentException {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {

            return ResponseEntity.notFound().build();
        }

        byte[] pdfBytes = generateBookingPdf(booking);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "booking.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/exportexcel")
    public void exportOrdersToExcel(HttpServletResponse response) {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=orders.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Orders");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Номер заказа");
        headerRow.createCell(1).setCellValue("Номер пользователя");
        headerRow.createCell(2).setCellValue("Название тура");
        headerRow.createCell(3).setCellValue("Доп услуга");
        headerRow.createCell(4).setCellValue("Дата 1");
        headerRow.createCell(5).setCellValue("Дата 2");
        headerRow.createCell(6).setCellValue("Общая стоимость");


        List<Booking> bookingList = bookingRepository.findAll();

        int rowNum = 1;
        CellStyle dateCellStyle = workbook.createCellStyle();
        short dateFormat = workbook.createDataFormat().getFormat("dd.MM.yyyy");
        dateCellStyle.setDataFormat(dateFormat);
        for (Booking booking : bookingList) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(booking.getId());
            dataRow.createCell(1).setCellValue(booking.getUser().getId());
            dataRow.createCell(2).setCellValue(booking.getTour().getTitle());
            if (booking.getAdditionalService() != null)
                dataRow.createCell(3).setCellValue(booking.getAdditionalService().getName());
            else
                dataRow.createCell(3).setCellValue("");
            Cell check_in_date_cell = dataRow.createCell(4);
            check_in_date_cell.setCellValue(booking.getCheckInDate());
            check_in_date_cell.setCellStyle(dateCellStyle);
            Cell check_out_date_cell = dataRow.createCell(5);
            check_out_date_cell.setCellValue(booking.getCheckOutDate());
            check_out_date_cell.setCellStyle(dateCellStyle);
            if (booking.getAdditionalService() != null)
                dataRow.createCell(6).setCellValue(booking.getPrice().doubleValue() + booking.getAdditionalService().getPrice().doubleValue());
            else
                dataRow.createCell(6).setCellValue(booking.getPrice().doubleValue());

        }

        // Write the workbook to the response output stream
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


