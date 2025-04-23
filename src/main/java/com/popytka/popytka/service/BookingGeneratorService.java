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
}
