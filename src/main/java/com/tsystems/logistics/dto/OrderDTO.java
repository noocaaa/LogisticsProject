package com.tsystems.logistics.dto;

import java.util.Set;

public class OrderDTO {
    private Integer id;
    private Boolean completed;
    private TruckDTO truck;
    private Set<DriverDTO> drivers;
    private Set<WaypointDTO> waypoints;

    private Integer waypointsCount;

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

    public TruckDTO getTruck() {
        return truck;
    }

    public void setTruck(TruckDTO truck) {
        this.truck = truck;
    }

    public Set<DriverDTO> getDrivers() {
        return drivers;
    }

    public void setDrivers(Set<DriverDTO> drivers) {
        this.drivers = drivers;
    }

    public Set<WaypointDTO> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(Set<WaypointDTO> waypoints) {
        this.waypoints = waypoints;
    }

    public Integer getWaypointsCount() {
        return waypointsCount;
    }

    public void setWaypointsCount(Integer waypointsCount) {
        this.waypointsCount = waypointsCount;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", completed=" + completed +
                ", truck=" + truck +
                ", drivers=" + drivers +
                ", waypoints=" + waypoints +
                ", waypointsCount=" + waypointsCount +
                '}';
    }

}
