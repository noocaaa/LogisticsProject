package com.tsystems.logistics.dto;

public class TruckDTO {
    private Integer id;
    private String number;
    private Integer capacity;
    private String status;
    private String currentCity;

    // Getter y Setter para id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    // Getter y Setter para number
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    // Getter y Setter para capacity
    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    // Getter y Setter para status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter y Setter para currentCity
    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }
}
