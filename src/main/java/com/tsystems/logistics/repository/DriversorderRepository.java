package com.tsystems.logistics.repository;

import com.tsystems.logistics.entities.Driversorder;
import com.tsystems.logistics.entities.DriversorderId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriversorderRepository extends JpaRepository<Driversorder, DriversorderId> {

    // Optional Consulting Methods
    List<Driversorder> findByDriverId(Integer driverId);
}