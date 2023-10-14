package com.tsystems.logistics.entities;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "trucks", schema = "public", indexes = {
        @Index(name = "trucks_number_key", columnList = "number", unique = true)
})
public class Truck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "truck_id", nullable = false)
    private Integer id;

    @Column(name = "number", length = 7)
    private String number;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "status", length = 3)
    private String status;

    @Column(name = "current_city", length = 50)
    private String currentCity;

    @OneToMany(mappedBy = "currentTruck")
    private Set<Driver> drivers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "truck")
    private Set<Order> orders = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }

    public Set<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(Set<Driver> drivers) {
        this.drivers = drivers;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

}