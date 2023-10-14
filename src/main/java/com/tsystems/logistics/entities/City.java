package com.tsystems.logistics.entities;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "cities", schema = "public", indexes = {
        @Index(name = "cities_name_key", columnList = "name", unique = true)
})
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id", nullable = false)
    private Integer id;

    @Column(name = "name", length = 50)
    private String name;

    @OneToMany(mappedBy = "city1")
    private Set<Distance> cityDistancesFrom;

    @OneToMany(mappedBy = "city2")
    private Set<Distance> cityDistancesTo;

    @OneToMany(mappedBy = "city")
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

    public Set<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(Set<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

}