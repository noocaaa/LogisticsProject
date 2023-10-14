package com.tsystems.logistics.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "distances", schema = "public")
public class Distance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "distance_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city1_id")
    private City city1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city2_id")
    private City city2;

    @Column(name = "distance")
    private Integer distance;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public City getCity1() {
        return city1;
    }

    public void setCity1(City city1) {
        this.city1 = city1;
    }

    public City getCity2() {
        return city2;
    }

    public void setCity2(City city2) {
        this.city2 = city2;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

}