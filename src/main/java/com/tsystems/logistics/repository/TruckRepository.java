package com.tsystems.logistics.repository;

import com.tsystems.logistics.entities.Truck;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface TruckRepository extends JpaRepository<Truck, Integer> {

    // Optional Consulting Methods
    Truck findByNumber(String number);

    Page<Truck> findByNumberContaining(String number, Pageable pageable);

    int countByStatus(String status);

}
