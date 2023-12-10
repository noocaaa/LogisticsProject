package com.tsystems.logistics.dto;

public class DistanceDTO {
    private Integer id;
    private CityDTO city1;
    private CityDTO city2;
    private Integer distance;

    // Constructor por defecto
    public DistanceDTO() {
    }

    // Constructor con todos los campos
    public DistanceDTO(Integer id, CityDTO city1, CityDTO city2, Integer distance) {
        this.id = id;
        this.city1 = city1;
        this.city2 = city2;
        this.distance = distance;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CityDTO getCity1() {
        return city1;
    }

    public void setCity1(CityDTO city1) {
        this.city1 = city1;
    }

    public CityDTO getCity2() {
        return city2;
    }

    public void setCity2(CityDTO city2) {
        this.city2 = city2;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }
}
