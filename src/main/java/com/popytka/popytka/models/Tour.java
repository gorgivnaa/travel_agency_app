package com.popytka.popytka.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
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

    public LocalDate getCheck_in_date() {
        return check_in_date;
    }

    public void setCheck_in_date(LocalDate check_in_date) {
        this.check_in_date = check_in_date;
    }

    public LocalDate getCheck_out_date() {
        return check_out_date;
    }

    public void setCheck_out_date(LocalDate check_out_date) {
        this.check_out_date = check_out_date;
    }

    public Long getTour_id() {
        return tour_id;
    }

    public void setTour_id(Long tour_id) {
        this.tour_id = tour_id;
    }

    public int getPlace_quantity() {
        return place_quantity;
    }

    public void setPlace_quantity(int place_quantity) {
        this.place_quantity = place_quantity;
    }

    public Long getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(Long hotel_id) {
        this.hotel_id = hotel_id;
    }

    public Long getCountry_id() {
        return country_id;
    }

    public void setCountry_id(Long country_id) {
        this.country_id = country_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
