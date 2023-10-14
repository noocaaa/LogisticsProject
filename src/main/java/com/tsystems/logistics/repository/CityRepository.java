package com.tsystems.logistics.repository;

import com.tsystems.logistics.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {

    // Optional Consulting Methods
    City findByName(String name);
}
