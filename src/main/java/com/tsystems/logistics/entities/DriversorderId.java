package com.tsystems.logistics.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DriversorderId implements Serializable {
    private static final long serialVersionUID = -719191563013545901L;
    @Column(name = "driver_id", nullable = false)
    private Integer driverId;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DriversorderId entity = (DriversorderId) o;
        return Objects.equals(this.driverId, entity.driverId) &&
                Objects.equals(this.orderId, entity.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId, orderId);
    }

}