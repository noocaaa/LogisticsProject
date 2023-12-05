package com.tsystems.logistics.entities;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "orders", schema = "public")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Integer id;

    @Column(name = "completed")
    private Boolean completed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_id")
    private Truck truck;

    @ManyToMany(mappedBy = "orders")
    private Set<Driver> drivers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Waypoint> waypoints = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }

    public Set<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(Set<Driver> drivers) {
        this.drivers = drivers;
    }

    public Set<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(Set<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

}