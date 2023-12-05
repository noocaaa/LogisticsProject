package com.tsystems.logistics.dto;

public class CargoDTO {
    private Integer id;
    private String name;
    private Integer weight;
    private String status;

    public CargoDTO() {
    }

    // Constructor con todos los campos
    public CargoDTO(Integer id, String name, Integer weight, String status) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.status = status;
    }

    // Getters y setters
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

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CargoDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                ", status='" + status + '\'' +
                '}';
    }
}
