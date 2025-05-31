package com.popytka.popytka.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.popytka.popytka.entity.AdditionalService;
import com.popytka.popytka.entity.Booking;
import com.popytka.popytka.entity.Hotel;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.entity.User;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class BookingGeneratorService {

    public byte[] generateBookingPdf(Booking booking) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setInitialLeading(16);

            BaseFont baseFont = BaseFont.createFont("src/main/resources/fonts/font.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 16, Font.BOLD);
            Font boldFont = new Font(baseFont, 12, Font.BOLD);
            Font regularFont = new Font(baseFont, 12);

            document.open();

            // Заголовок агентства
            document.add(new Paragraph("ТУРИСТИЧЕСКАЯ КОМПАНИЯ \"АНТУР\"", titleFont));
            document.add(new Paragraph("г. Минск, ул. Максима Богдановича, д. 155", regularFont));
            document.add(new Paragraph("Тел.: +375 17 360-24-14 | Email: info@antour.by", regularFont));
            document.add(new Paragraph("------------------------------------------------------------", regularFont));

            // Название путёвки
            document.add(new Paragraph("ПУТЁВКА № " + booking.getId(), boldFont));
            document.add(new Paragraph("Дата оформления: " + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")), regularFont));
            document.add(new Paragraph(" ", regularFont));

            // Информация о клиенте
            User user = booking.getUser();
            document.add(new Paragraph("ФИО клиента: " + user.getLastName() + " " + user.getFirstName(), regularFont));
            document.add(new Paragraph("Контактный номер: " + user.getPhone(), regularFont));
            document.add(new Paragraph("Email: " + user.getEmail(), regularFont));
            document.add(new Paragraph("Количество мест: " + booking.getPlaceQuantity(), regularFont));
            document.add(new Paragraph("------------------------------------------------------------", regularFont));

            // Тур и отель
            Tour tour = booking.getTour();
            Hotel hotel = booking.getHotel();
            document.add(new Paragraph("Название тура: " + tour.getTitle(), boldFont));
            document.add(new Paragraph("Отель: " + hotel.getName(), regularFont));
            document.add(new Paragraph("Рейтинг отеля: " + hotel.getRating(), regularFont));
            document.add(new Paragraph("Описание тура: " + tour.getDescription(), regularFont));
            document.add(new Paragraph(" ", regularFont));

            // Даты и услуги
            document.add(new Paragraph("Даты поездки:", boldFont));
            document.add(new Paragraph("• Заезд: " + booking.getCheckInDate().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")), regularFont));
            document.add(new Paragraph("• Возвращение: " + booking.getCheckOutDate().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")), regularFont));
            document.add(new Paragraph(" ", regularFont));

            // Дополнительные услуги
            AdditionalService service = booking.getAdditionalService();
            document.add(new Paragraph("Дополнительные услуги:", boldFont));
            if (service != null) {
                document.add(new Paragraph("• " + service.getName() + " — " + service.getPrice() + " руб.", regularFont));
            } else {
                document.add(new Paragraph("• Отсутствуют", regularFont));
            }

            document.add(new Paragraph("------------------------------------------------------------", regularFont));
            document.add(new Paragraph("Общая стоимость путёвки: " + String.format("%,.2f руб.", booking.getPrice()), boldFont));

            // Подписи
            document.add(new Paragraph(" ", regularFont));
            document.add(new Paragraph("Подпись клиента: ____________________", regularFont));
            document.add(new Paragraph("М.П. / Подпись представителя агентства: ____________________", regularFont));

            document.close();
        } catch (DocumentException e) {
            throw new IOException("Error creating PDF file", e);
        }

        return baos.toByteArray();
    }

}
