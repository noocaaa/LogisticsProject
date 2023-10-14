package com.tsystems.logistics.repository;

import com.tsystems.logistics.entities.Distance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DistanceRepository extends JpaRepository<Distance, Integer> {

    // Optional Consulting Methods
    Optional<Distance> findByCity1NameAndCity2Name(String city1Name, String city2Name);
}
