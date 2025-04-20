package com.popytka.popytka.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "tours")
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tour_id;
    private Long hotel_id, country_id;
    private String title, description;
    private double price;
    private int place_quantity;
    private LocalDate check_in_date, check_out_date;
    public Tour() {
    }

    public Tour(Long tour_id, Long hotel_id, Long country_id, String title, String description, double price, int place_quantity, LocalDate check_in_date, LocalDate check_out_date) {
        this.tour_id = tour_id;
        this.hotel_id = hotel_id;
        this.country_id = country_id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.place_quantity = place_quantity;
        this.check_in_date = check_in_date;
        this.check_out_date = check_out_date;
    }
}
