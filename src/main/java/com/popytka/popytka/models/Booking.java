package com.popytka.popytka.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long booking_id;

    @ManyToOne
    @JoinColumn(name = "tour_id")
    private Tour tour;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    private int place_quantity;
    private double price;
    private LocalDate check_in_date, check_out_date;

    public Booking(Tour tour, User user, Hotel hotel, Service service, int place_quantity, double price, LocalDate check_in_date, LocalDate check_out_date) {
        this.tour = tour;
        this.user = user;
        this.hotel = hotel;
        this.service = service;
        this.place_quantity = place_quantity;
        this.price = price;
        this.check_in_date = check_in_date;
        this.check_out_date = check_out_date;
    }
}

