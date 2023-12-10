package com.tsystems.logistics.repository;

import com.tsystems.logistics.entities.Waypoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaypointRepository extends JpaRepository<Waypoint, Integer> {

    List<Waypoint> findByOrderId(Integer orderId);
}
