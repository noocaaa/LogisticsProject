package com.tsystems.logistics.dto;

import java.util.Set;
import java.util.HashSet;
import java.util.List;

public class DriverDTO {
    private Integer id;
    private String name;
    private String surname;
    private String personalNumber;
    private Integer workingHours;
    private String status;
    private String currentCity;
    private TruckDTO currentTruck;
    private Set<OrderDTO> orders;

    private String truckNumber;
    private List<String> coDriversId;
    private String orderNumber;
    private List<WaypointDTO> waypoints;

    public DriverDTO() {
        this.currentTruck = null;
        this.orders = new HashSet<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public Integer getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(Integer workingHours) {
        this.workingHours = workingHours;
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

    public TruckDTO getCurrentTruck() {
        return currentTruck;
    }

    public void setCurrentTruck(TruckDTO currentTruck) {
        this.currentTruck = currentTruck;
    }

    public Set<OrderDTO> getOrders() {
        return orders;
    }

    public void setOrders(Set<OrderDTO> orders) {
        this.orders = orders;
    }

    public String getTruckNumber() {
        return truckNumber;
    }

    public void setTruckNumber(String truckNumber) {
        this.truckNumber = truckNumber;
    }

    public List<String> getCoDrivers() {
        return coDriversId;
    }

    public void setCoDrivers(List<String> coDrivers) {
        this.coDriversId = coDrivers;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<WaypointDTO> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<WaypointDTO> waypoints) {
        this.waypoints = waypoints;
    }
}
