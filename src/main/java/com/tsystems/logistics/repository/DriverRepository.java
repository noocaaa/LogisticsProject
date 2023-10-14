package com.tsystems.logistics.repository;

import com.tsystems.logistics.entities.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer> {

    // Optional Consulting Methods
    Optional<Driver> findByPersonalNumber(String personalNumber);
    Optional<Driver> findByNameAndSurname(String name, String surname);
}
