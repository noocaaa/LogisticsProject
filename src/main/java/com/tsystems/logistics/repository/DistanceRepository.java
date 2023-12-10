package com.tsystems.logistics.repository;

import com.tsystems.logistics.entities.Distance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


import com.tsystems.logistics.entities.City;

@Repository
public interface DistanceRepository extends JpaRepository<Distance, Integer> {

    Optional<Distance> findByCity1NameAndCity2Name(String city1Name, String city2Name);

    Distance findDistanceByCity1_IdAndAndCity2_Id(Integer city1Id, Integer city2Id);

}
