package com.tsystems.logistics.dto;

import java.util.Date;

public class OrderDTO {
    private Integer id;
    private String status;
    private String description;
    private Date creationDate;
    private DriverDTO driver;
    private TruckDTO truck;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public DriverDTO getDriver() {
        return driver;
    }

    public void setDriver(DriverDTO driver) {
        this.driver = driver;
    }

    public TruckDTO getTruck() {
        return truck;
    }

    public void setTruck(TruckDTO truck) {
        this.truck = truck;
    }
}
