package com.tsystems.logistics.repository;

import com.tsystems.logistics.entities.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Integer> {

    // Optional Consulting Methods
    Cargo findByName(String name);

    int countByStatus(String status);
}
