package com.popytka.popytka.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long order_id;
    @ManyToOne
    @JoinColumn(name = "tour_id")
    private Tour tour;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;
    private LocalDateTime order_date;
    private int place_quantity;

    public Orders() {
    }

    public Orders(Tour tour, User user, Service service, LocalDateTime order_date, int place_quantity) {
        this.tour = tour;
        this.user = user;
        this.service = service;
        this.order_date = order_date;
        this.place_quantity = place_quantity;
    }

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public LocalDateTime getOrder_date() {
        return order_date;
    }

    public void setOrder_date(LocalDateTime order_date) {
        this.order_date = order_date;
    }

    public int getPlace_quantity() {
        return place_quantity;
    }

    public void setPlace_quantity(int place_quantity) {
        this.place_quantity = place_quantity;
    }
}
