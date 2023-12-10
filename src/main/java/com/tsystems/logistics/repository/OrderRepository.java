package com.tsystems.logistics.repository;

import com.tsystems.logistics.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    Optional<Order> findTopByTruckIdOrderByIdDesc(Integer truckId);

    Optional<Order> findByTruckId(Integer integer);

    int countByCompleted(boolean completed);

}
