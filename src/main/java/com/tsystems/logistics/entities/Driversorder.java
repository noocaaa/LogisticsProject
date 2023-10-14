package com.tsystems.logistics.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "driversorders", schema = "public")
public class Driversorder {
    @EmbeddedId
    private DriversorderId id;

    @MapsId("driverId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @MapsId("orderId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public DriversorderId getId() {
        return id;
    }

    public void setId(DriversorderId id) {
        this.id = id;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

}