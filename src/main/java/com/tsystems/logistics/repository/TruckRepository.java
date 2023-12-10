package com.tsystems.logistics.repository;

import com.tsystems.logistics.entities.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TruckRepository extends JpaRepository<Truck, Integer> {

    // Optional Consulting Methods
    Truck findByNumber(String number);

    int countByStatus(String status);

}
