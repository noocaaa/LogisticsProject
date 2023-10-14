package com.tsystems.logistics.entities;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "cargos", schema = "public")
public class Cargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cargo_id", nullable = false)
    private Integer id;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "status", length = 15)
    private String status;

    @OneToMany(mappedBy = "cargo")
    private Set<Waypoint> waypoints = new LinkedHashSet<>();

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

    public Set<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(Set<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

}