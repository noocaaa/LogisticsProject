package com.tsystems.logistics.repository;

import com.tsystems.logistics.entities.Waypoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaypointRepository extends JpaRepository<Waypoint, Integer> {

    // Optional Consulting Methods
    List<Waypoint> findByOrder_Id(Integer orderId);

    List<Waypoint> findByCity_Id(Integer cityId);

    List<Waypoint> findByCargo_Id(Integer cargoId);

    List<Waypoint> findByType(String type);
}
