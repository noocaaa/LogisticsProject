package com.tsystems.logistics.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DriversorderId implements Serializable {
    private static final long serialVersionUID = -719191563013545901L;

    private Integer driverId;
    private Integer orderId;

    public DriversorderId() {
    }

    public DriversorderId(Integer driverId, Integer orderId) {
        this.driverId = driverId;
        this.orderId = orderId;
    }

    // Getters y setters
    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DriversorderId that = (DriversorderId) o;
        return Objects.equals(driverId, that.driverId) &&
                Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId, orderId);
    }
}