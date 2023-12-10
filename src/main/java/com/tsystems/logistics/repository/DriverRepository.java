package com.tsystems.logistics.repository;

import com.tsystems.logistics.entities.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer> {

    Optional<Driver> findByPersonalNumber(String personalNumber);

    List<Driver> findByCurrentTruckId(Integer truckId);

    int countByWorkingHours(int hours);
}
