package com.tsystems.logistics.dto;

import com.tsystems.logistics.service.CityService;
public class WaypointDTO {
    private Integer id;
    private Integer orderId;
    private Integer cityId;
    private Integer cargoId;
    private String type;

    private String cityName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getCityId() {
        return this.cityId;
    }

    public String getCityName() {
        return this.cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getCargoId() {
        return cargoId;
    }

    public void setCargoId(Integer cargoId) {
        this.cargoId = cargoId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
