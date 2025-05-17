package com.popytka.popytka.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "password")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ManagerTour> managedTours = new HashSet<>();

    public void addTour(Tour tour) {
        ManagerTour managerTour = ManagerTour.builder()
                .manager(this)
                .tour(tour)
                .build();
        managedTours.add(managerTour);
        tour.getManagers().add(managerTour);
    }

    public void removeTour(Tour tour) {
        ManagerTour managerTour = findManagerTour(tour);
        if (managerTour != null) {
            managedTours.remove(managerTour);
            tour.getManagers().remove(managerTour);
        }
    }

    private ManagerTour findManagerTour(Tour tour) {
        return managedTours.stream()
                .filter(mt -> mt.getTour().equals(tour))
                .findFirst()
                .orElse(null);
    }
}
